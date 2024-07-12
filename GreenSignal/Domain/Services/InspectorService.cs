using Data.Models;
using Data.Repositories;
using Domain.Exceptions;
using Infrastructure;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Options;
using System.Text;
using System.Security.Cryptography;
using System.Transactions;
using MailManager;

namespace Domain.Services
{
    public interface IInspectorService
    {
        Task<Inspector?> GetByIdAsync(Guid id);
        Task<Inspector> GetByPhoneAsync(string phone);
        Task<Inspector> UpdateInspectorAsync(Guid id, Inspector updatedInspector);
        Task<Inspector> CreateInspectorAsync(Inspector newInspector, IFormFile inspectorPhoto, IFormFile certificatePhoto);
        Task<IEnumerable<Inspector>> GetActiveInspectorsAsync();
        Task<Inspector?> GetInspectorByTelegramUserIdAsync(string telegramUserId);
        Task UpdateInspectorLocationAsync(double lat, double lng, Inspector inspector);
        Task<Inspector> UpdateInspectorPhotoAsync(Guid inspectorId, IFormFile inspectorPhoto);
        Task<Inspector> UpdateInspectorCertificateAsync(Guid inspectorId, IFormFile certificatePhoto);
        Task<Inspector> UpdateSignatureAsync(Guid inspectorId, IFormFile signature);
        Task AttachInspectorTelegramAsync(string phone, string telegramId, string chatId);
        Task<bool> CheckInspectorTelegramIdAsync(Guid inspectorId);
    }

    public class InspectorService : IInspectorService
    {
        private readonly IInspectorRepository _inspectorRepository;
        private readonly IOptions<GreenSignalConfigurationOptions> _greenSignalConfigurationOptions;
        private readonly ISavedFileService _savedFileService;
        private readonly IMailManagerService _mailManagerService;

        public InspectorService(IInspectorRepository inspectorRepository,
                                IOptions<GreenSignalConfigurationOptions> greenSignalConfigurationOptions,
                                ISavedFileService savedFileService,
                                IMailManagerService mailManagerService)
        {
            _inspectorRepository = inspectorRepository;
            _greenSignalConfigurationOptions = greenSignalConfigurationOptions;
            _savedFileService = savedFileService;
            _mailManagerService = mailManagerService;
        }

        public async Task<Inspector> CreateInspectorAsync(Inspector newInspector, IFormFile inspectorPhoto, IFormFile certificatePhoto)
        {
            var inspector = await _inspectorRepository.GetByPhoneAsync(newInspector.Phone).ConfigureAwait(false);
            if (inspector != null) throw new InspectorAlreadyExistsException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            using Stream fileCertStream = certificatePhoto.OpenReadStream();
            using Stream filePhotoStream = inspectorPhoto.OpenReadStream();

            var certificate = await _savedFileService.CreateSavedFileAsync(fileCertStream, certificatePhoto.FileName, SavedFileType.Certificate).ConfigureAwait(false);
            var photo = await _savedFileService.CreateSavedFileAsync(filePhotoStream, inspectorPhoto.FileName, SavedFileType.Photo).ConfigureAwait(false);

            newInspector.Id = Guid.NewGuid();
            newInspector.CreatedAt = DateTime.UtcNow;
            newInspector.ReviewStatus = InspectorStatus.Created;
            newInspector.CertificateFileId = certificate.Id; 
            newInspector.PhotoFileId = photo.Id;
            newInspector.Number = await _inspectorRepository.GetNextNumberAsync().ConfigureAwait(false); 
            newInspector.InternalEmail = $"inspector-{newInspector.Number}@{_greenSignalConfigurationOptions.Value.Mailcow.Domain}";
            newInspector.Password = GenerateRandomPassword();

            await _inspectorRepository.CreateInspectorAsync(newInspector).ConfigureAwait(false);
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine($"inspector-{newInspector.Number}");
            Console.ForegroundColor = ConsoleColor.White;
            await _mailManagerService.CreateMail($"inspector-{newInspector.Number}", newInspector.Password, newInspector.FIO).ConfigureAwait(false);

            fileCertStream.Close();
            filePhotoStream.Close();

            transaction.Complete();
            transaction.Dispose();

            return newInspector;
        }

        private static string GenerateRandomPassword()
        {
            int size = 30;
            string a = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()_-=+";
            StringBuilder result = new(size);
            using var rng = new RNGCryptoServiceProvider();
            while (result.Length < size)
            {
                var bytes = new byte[1];
                rng.GetBytes(bytes);
                if (bytes[0] >= (byte)(a.Length - 1)) continue;
                result.Append(a[bytes[0]]);
            }
            return result.ToString();
        }

        public async Task<Inspector?> GetByIdAsync(Guid id)
        {
            var inspector = await _inspectorRepository.GetByIdAsync(id).ConfigureAwait(false);

            return inspector;
        }

        public async Task<Inspector> GetByPhoneAsync(string phone)
        {
            var inspector = await _inspectorRepository.GetByPhoneAsync(phone).ConfigureAwait(false);

            return inspector ?? throw new InspectorNotFoundException();
        }

        public async Task<Inspector> UpdateInspectorAsync(Guid id, Inspector updatedInspector)
        {
            var inspector = await _inspectorRepository.GetByIdAsync(id).ConfigureAwait(false) ?? throw new InspectorNotFoundException();

            inspector.UpdatedAt = DateTime.UtcNow;
            inspector.FIO = updatedInspector.FIO;
            inspector.Phone = updatedInspector.Phone;
            inspector.CertificateId = updatedInspector.CertificateId;
            inspector.CertificateDate = updatedInspector.CertificateDate;
            inspector.SchoolId = updatedInspector.SchoolId;

            await _inspectorRepository.UpdateInspectorAsync(inspector).ConfigureAwait(false);

            return inspector;
        }

        public async Task<IEnumerable<Inspector>> GetActiveInspectorsAsync()
        {
            return await _inspectorRepository.GetActiveInspectorsAsync().ConfigureAwait(false);
        }

        public async Task<Inspector?> GetInspectorByTelegramUserIdAsync(string telegramUserId)
        {
            return await _inspectorRepository.GetInspectorByTelegramUserIdAsync(telegramUserId).ConfigureAwait(false);
        }

        public async Task UpdateInspectorLocationAsync(double lat, double lng, Inspector inspector)
        {
            inspector.LastLatLngAt = DateTime.UtcNow;
            inspector.Lat = lat;
            inspector.Lng = lng;
            await _inspectorRepository.UpdateInspectorAsync(inspector).ConfigureAwait(false);
        }

        public async Task<Inspector> UpdateInspectorPhotoAsync(Guid inspectorId, IFormFile inspectorPhoto)
        {
            var inspector = await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false) ?? throw new InspectorNotFoundException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            using Stream filePhotoStream = inspectorPhoto.OpenReadStream();
            var photo = await _savedFileService.CreateSavedFileAsync(filePhotoStream, inspectorPhoto.FileName, SavedFileType.Photo).ConfigureAwait(false);
            inspector.PhotoFileId = photo.Id;
            
            await _inspectorRepository.UpdateInspectorAsync(inspector).ConfigureAwait(false);

            filePhotoStream.Close();

            transaction.Complete();
            transaction.Dispose();

            return await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false) ?? throw new InspectorNotFoundException();
        }

        public async Task<Inspector> UpdateInspectorCertificateAsync(Guid inspectorId, IFormFile certificatePhoto)
        {
            var inspector = await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false) ?? throw new InspectorNotFoundException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);
            
            using Stream fileCertStream = certificatePhoto.OpenReadStream();
            var certificate = await _savedFileService.CreateSavedFileAsync(fileCertStream, certificatePhoto.FileName, SavedFileType.Certificate).ConfigureAwait(false);
            inspector.CertificateFileId = certificate.Id;
            
            await _inspectorRepository.UpdateInspectorAsync(inspector).ConfigureAwait(false);

            fileCertStream.Close();

            transaction.Complete();
            transaction.Dispose();

            return await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false) ?? throw new InspectorNotFoundException();
        }

        public async Task<Inspector> UpdateSignatureAsync(Guid inspectorId, IFormFile signature)
        {
            var inspector = await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false) ?? throw new InspectorNotFoundException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            using Stream signatureStream = signature.OpenReadStream();

            var certificate = await _savedFileService.CreateSavedFileAsync(signatureStream, $"подпись-{inspector.FIO}.png", SavedFileType.Certificate).ConfigureAwait(false);

            inspector.SignatureId = certificate.Id;

            await _inspectorRepository.UpdateInspectorAsync(inspector).ConfigureAwait(false);

            signatureStream.Close();

            transaction.Complete();
            transaction.Dispose();

            return inspector;
        }

        public async Task AttachInspectorTelegramAsync(string phone, string telegramId, string chatId)
        {
            var inspector = await _inspectorRepository.GetByPhoneAsync(phone).ConfigureAwait(false)??throw new InspectorNotFoundException();

            inspector.TelegramUserId = telegramId;
            inspector.TelegramChatId = chatId;

            await _inspectorRepository.UpdateInspectorAsync(inspector).ConfigureAwait(false);
        }

        public async Task<bool> CheckInspectorTelegramIdAsync(Guid inspectorId)
        {
            var inspector = await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false)??throw new InspectorNotFoundException();

            return inspector.TelegramUserId != null;
        }
    }
}
