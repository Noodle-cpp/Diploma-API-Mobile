using Domain.Services;
using MailManager;
using MailManager.Exceptions;
using MailManager.ViewModels;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers
{
    [Route("[controller]")]
    [ApiController]
    //TODO: Контроллер для проверки (УДАЛИТЬ)
    public class EmailsController : ControllerBase
    {
        private readonly IMailManagerService _mailManagerService;
        private readonly IReceiveMessageService _receiveMessageService;

        public EmailsController(IMailManagerService mailManagerService,
                                IReceiveMessageService receiveMessageService)
        {
            _mailManagerService = mailManagerService;
            _receiveMessageService = receiveMessageService;
        }

        [HttpPost]
        public async Task<IActionResult> SendMail()
        {
            try
            {
                await _mailManagerService.SendMessage(new MailManager.ViewModels.EmailMessageViewModel()
                {
                    FromAddress = new EmailAddressViewModel()
                    {
                        Name = "Иванов Иван Иванович",
                        Address = "admin@mail.greensignal.dev"
                    },
                    ToAddress = new EmailAddressViewModel()
                    {
                        Name = "Шибанова Валентина Сергеевна",
                        Address = "inspector-1@mail.greensignal.dev"
                    },
                    Content = "Привевт, МИР!!!",
                    Subject = "Приветствие",
                    Attachments = new List<AttachmentViewModel>()
                    {
                        //new MailManager.ViewModels.AttachmentViewModel()
                        //{
                        //    OriginalName = "cert.jpg",
                        //    Data = StreamToByteArray(await _fileManagerService.Download("Certificates%2F3bb249f8-e407-4b12-8140-ce544085617a.jpg").ConfigureAwait(false))
                        //}
                    }
                });
                
                return Ok();
            }
            catch (AddressIsEmptyException)
            {
                return UnprocessableEntity();
            }
            catch (ContentIsEmptyException)
            {
                return UnprocessableEntity();
            }
        }

        [HttpGet]
        public async Task<IActionResult> ParseMail()
        {
            var mails = await _mailManagerService.GetMailsByAddress(new EmailAccountViewModel()
            {
                Address = "admin@mail.greensignal.dev",
                Password = "adminadmin"
            }).ConfigureAwait(false);

            await _receiveMessageService.ParseEmailMessagesToDBAsync(mails, Guid.Parse("12700db7-2194-4a87-959a-418b9cc72ec0")).ConfigureAwait(false);

            return Ok(mails);
        }

        [HttpPost("Address")]
        public async Task<IActionResult> CreateMail()
        {
            await _mailManagerService.CreateMail("inspector_test", "testpassword", "Шибанова Валентина Сергеевна").ConfigureAwait(false);
            return Ok();
        }
    }
}
