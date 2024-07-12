using Data.Models;
using Data.Repositories;
using Domain.Exceptions;
using Infrastructure;
using MailManager.ViewModels;
using Microsoft.Extensions.Options;
using PushNotification;
using PushNotification.ViewModels;

namespace Domain.Services
{
    public interface IReceiveMessageService
    {
        Task ParseEmailMessagesToDBAsync(IEnumerable<EmailMessageViewModel> emailMessages, Guid inspectorId);
        Task<ReceiveMessage> GetReceiveMessageByIdAsync(Guid id, Guid inspectorId);
        Task AttachMessageToPetitionAsync(Guid messageId, Guid petitionId, Guid inspectorId);
        Task DetachMessageFromPetitionAsync(Guid messageId, Guid petitionId, Guid inspectorId);
        Task<ReceiveMessage> MarkMessageAsSeenAsync(Guid messageId, Guid inspectorId);
        Task<IEnumerable<ReceiveMessage>> GetInspectorMessages(Guid inspectorId, string? filter);
    }

    public class ReceiveMessageService : IReceiveMessageService
    {
        private readonly IReceiveMessageRepository _receiveMessageRepository;
        private readonly ISavedFileService _savedFileService;
        private readonly IPetitionRepository _petitionRepository;
        private readonly INotificationGateway _notificationGateway;
        private readonly IInspectorSessionService _inspectorSessionService;
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly IInspectorRepository _inspectorRepository;
        private readonly string _telegramToken;

        public ReceiveMessageService(IReceiveMessageRepository receiveMessageRepository,
                                        ISavedFileService savedFileService,
                                        IPetitionRepository petitionRepository,
                                        INotificationGateway notificationGateway,
                                        IInspectorSessionService inspectorSessionService,
                                        IOptions<GreenSignalConfigurationOptions> options,
                                        IHttpClientFactory httpClientFactory,
                                        IInspectorRepository inspectorRepository)
        {
            _receiveMessageRepository = receiveMessageRepository;
            _savedFileService = savedFileService;
            _petitionRepository = petitionRepository;
            _notificationGateway = notificationGateway;
            _inspectorSessionService = inspectorSessionService;
            _httpClientFactory = httpClientFactory;
            _telegramToken = options.Value.TelegramBot.Token;
            _inspectorRepository = inspectorRepository;
        }

        public async Task AttachMessageToPetitionAsync(Guid messageId, Guid petitionId, Guid inspectorId)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(petitionId).ConfigureAwait(false);

            if (petition == null || petition.Status == PetitionStatus.Archived) throw new PetitionNotFoundException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            var message = await _receiveMessageRepository.GetReceiveMessageByIdAsync(messageId).ConfigureAwait(false) ?? throw new ReceiveMessageNotFoundException();
            if (message.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            if (message.PetitionId != null) throw new ReceiveMessageIsAlreadyAttachedException();

            message.PetitionId = petitionId;
            await _receiveMessageRepository.UpdateReceiveMessageAsync(message).ConfigureAwait(false);
        }
        public async Task DetachMessageFromPetitionAsync(Guid messageId, Guid petitionId, Guid inspectorId)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(petitionId).ConfigureAwait(false);

            if (petition == null || petition.Status == PetitionStatus.Archived) throw new PetitionNotFoundException();
            if (petition.Status == PetitionStatus.Success || petition.Status == PetitionStatus.Failed) throw new PetitionIsAlreadyCloseException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            var message = await _receiveMessageRepository.GetReceiveMessageByIdAsync(messageId).ConfigureAwait(false) ?? throw new ReceiveMessageNotFoundException();
            if (message.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            message.PetitionId = null;
            await _receiveMessageRepository.UpdateReceiveMessageAsync(message).ConfigureAwait(false);
        }

        public async Task<IEnumerable<ReceiveMessage>> GetInspectorMessages(Guid inspectorId, string? filter)
        {
            return await _receiveMessageRepository.GetInspectorMessagesAsync(inspectorId, filter).ConfigureAwait(false);
        }

        public async Task<ReceiveMessage> GetReceiveMessageByIdAsync(Guid id, Guid inspectorId)
        {
            var message = await _receiveMessageRepository.GetReceiveMessageByIdAsync(id).ConfigureAwait(false) ?? throw new ReceiveMessageNotFoundException();

            if(message.InspectorId != inspectorId)
                throw new InspectorNotAnOwnerException();

            return message;
        }

        public async Task<ReceiveMessage> MarkMessageAsSeenAsync(Guid messageId, Guid inspectorId)
        {
            var message = await _receiveMessageRepository.GetReceiveMessageByIdAsync(messageId).ConfigureAwait(false) ?? throw new ReceiveMessageNotFoundException();
            
            if (message.InspectorId != inspectorId)
                throw new InspectorNotAnOwnerException();

            message.Seen = true;

            await _receiveMessageRepository.UpdateReceiveMessageAsync(message).ConfigureAwait(false);

            return await _receiveMessageRepository.GetReceiveMessageByIdAsync(messageId).ConfigureAwait(false) ?? throw new ReceiveMessageNotFoundException();
        }

        public async Task ParseEmailMessagesToDBAsync(IEnumerable<EmailMessageViewModel> emailMessages, Guid inspectorId)
        {
            var receiveMessages = new List<ReceiveMessage>();

            foreach (var message in emailMessages)
            {
                Console.WriteLine(message.Subject);

                var newReceiveMessage = new ReceiveMessage()
                {
                    Id = Guid.NewGuid(),
                    Content = message.Content,
                    Seen = false,
                    FromAddress = message.FromAddress.Address,
                    FromName = message.FromAddress.Name,
                    InspectorId = inspectorId,
                    Subject = message.Subject,
                    CreatedAt = DateTime.UtcNow,
                    MessageAttachments = new List<MessageAttachment>()
                };

                foreach (var attachment in message.Attachments)
                {
                    var savedFile = await _savedFileService.CreateSavedFileAsync(new MemoryStream(attachment.Data), attachment.OriginalName, SavedFileType.MessageAttachment).ConfigureAwait(false) ?? throw new UploadAttachmentException();

                    newReceiveMessage.MessageAttachments.Add(new MessageAttachment()
                    {
                        Id = Guid.NewGuid(),
                        ReceiveMessageId = newReceiveMessage.Id,
                        SavedFileId = savedFile.Id
                    });
                }

                receiveMessages.Add(newReceiveMessage);

                var notification = new NotificationViewModel()
                {
                    Title = $"Новое сообщение от {newReceiveMessage.FromName}",
                    MessageType = MessageType.NewReceiveMessage,
                    Body = newReceiveMessage.Content,
                    ToId = newReceiveMessage.Id
                };


                var inspectorSessions = await _inspectorSessionService.GetInspectorsSessionsByInspectorIdAsync(inspectorId).ConfigureAwait(false);

                var inspector = await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false);
                if (inspector != null && inspector.TelegramChatId != null)
                    await SendMessage(inspector.TelegramChatId, $"Новое сообщение от {newReceiveMessage.FromName}", _telegramToken).ConfigureAwait(false);

                var result = await _notificationGateway.SendPushNotification(notification, inspectorSessions.Select(x => x.FirebaseToken)).ConfigureAwait(false);
                if (!result) Console.WriteLine("Уведомление отправлено с ошибкой");
            }


            await _receiveMessageRepository.CreateRangeReceiveMessagesAsync(receiveMessages).ConfigureAwait(false);
        }

        private async Task SendMessage(string chatId, string message, string token)
        {
            string url = $"https://api.telegram.org/bot{token}/sendMessage?" +
                         $"chat_id={chatId}&" +
                         $"text={message}";
            var httpClient = _httpClientFactory.CreateClient();
            var response = await httpClient.GetAsync(new Uri(url)).ConfigureAwait(false);
        }
    }
}
