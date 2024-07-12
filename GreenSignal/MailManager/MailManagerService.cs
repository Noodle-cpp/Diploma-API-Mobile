using Infrastructure;
using MailManager.ViewModels;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Options;
using MimeKit;
using MailKit;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Text;
using System.Threading.Tasks;
using MailManager.Exceptions;
using System.Net.Security;
using System.Net;
using System.Security.Cryptography.X509Certificates;
using MailKit.Net.Imap;
using MailKit.Search;
using Org.BouncyCastle.Asn1.Crmf;
using System.Net.Http;
using System.Text.Json;
using System.Net.Http.Json;
using System.Net.Http.Headers;
using System.Drawing;

namespace MailManager
{
    public interface IMailManagerService
    {
        Task SendMessage(EmailMessageViewModel messageViewModel);
        Task<IEnumerable<EmailMessageViewModel>> GetMailsByAddress(EmailAccountViewModel accountViewModel);
        Task CreateMail(string localPart, string password, string name);
    }

    public class MailManagerService : IMailManagerService
    {
        private readonly int _smtpPort;
        private readonly string _smtpServer;
        private readonly int _imapPort;
        private readonly string _imapServer;
        private readonly string _domain;
        private readonly string _key;
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly JsonSerializerOptions _jsonSerializerOptions = new()
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
            IgnoreNullValues = true
        };

        public MailManagerService(IOptions<GreenSignalConfigurationOptions> configuration,
                                    IHttpClientFactory httpClientFactory)
        {
            _smtpPort = configuration.Value.Mailcow.SmtpPort;
            _smtpServer = configuration.Value.Mailcow.SmtpServer;
            _imapPort = configuration.Value.Mailcow.IMapPort;
            _imapServer = configuration.Value.Mailcow.IMapServer;
            _key = configuration.Value.Mailcow.Key;
            _httpClientFactory = httpClientFactory;
            _domain = configuration.Value.Mailcow.Domain;
        }

        /// <summary>
        /// Отправляет сообщение
        /// </summary>
        /// <param name="emailMessage"></param>
        /// <param name="emailLogin"></param>
        /// <returns></returns>
        public async Task SendMessage(EmailMessageViewModel messageViewModel)
        {
            if (string.IsNullOrWhiteSpace(messageViewModel.Content))
            {
                throw new ContentIsEmptyException();
            }
            if (messageViewModel.ToAddress == null || messageViewModel.FromAddress == null)
            {
                throw new AddressIsEmptyException();
            }

            var message = new MimeKit.MimeMessage();
            message.From.Add(new MailboxAddress(messageViewModel.FromAddress.Name, messageViewModel.FromAddress.Address));
            message.To.Add(new MailboxAddress(messageViewModel.ToAddress.Name, "department1@mail.greensignal.dev"));
            message.Subject = messageViewModel.Subject;

            BodyBuilder builder = new()
            {
                TextBody = messageViewModel.Content
            };
            foreach (var attachment in messageViewModel.Attachments)
                builder.Attachments.Add(attachment.OriginalName, attachment.Data);

            message.Body = builder.ToMessageBody();

            //Игнорирование сертификата 
            ServicePointManager.ServerCertificateValidationCallback = delegate (object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors sslPolicyErrors)
            {
                return true;
            };

            using (var client = new MailKit.Net.Smtp.SmtpClient())
            {
                await client.ConnectAsync(_smtpServer, _smtpPort, false).ConfigureAwait(false);

                await client.SendAsync(message).ConfigureAwait(false);
                await client.DisconnectAsync(true).ConfigureAwait(false);
            }
        }

        /// <summary>
        /// Получает непрочитанные сообщения на определенный адрес
        /// </summary>
        /// <param name="emailAddress">Адрес получателя</param>
        /// <returns></returns>
        public async Task<IEnumerable<EmailMessageViewModel>> GetMailsByAddress(EmailAccountViewModel accountViewModel)
        {
            var inspectoMails = new List<EmailMessageViewModel>();

            //Игнорирование сертификата 
            ServicePointManager.ServerCertificateValidationCallback = delegate (object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors sslPolicyErrors)
            {
                return true;
            };

            using (var client = new ImapClient())
            {
                client.Connect(_imapServer, _imapPort, false);

                client.AuthenticationMechanisms.Remove("XOAUTH2");
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine(accountViewModel.Address);
                Console.ForegroundColor = ConsoleColor.White;
                await client.AuthenticateAsync(accountViewModel.Address, accountViewModel.Password);

                // Получение списка всех писем в папке "Входящие"
                var inbox = client.Inbox;
                inbox.Open(FolderAccess.ReadWrite);
                var messages = await inbox.SearchAsync(SearchQuery.NotSeen).ConfigureAwait(false);

                foreach (var uid in messages)
                {   
                    var message = inbox.GetMessage(uid);
                    var newReceivedMessage = new EmailMessageViewModel()
                    {
                        FromAddress = new()
                        {
                            Name = message.From.OfType<MailboxAddress>().FirstOrDefault().Name,
                            Address = message.From.OfType<MailboxAddress>().FirstOrDefault().Address
                        },
                        ToAddress = new()
                        {
                            Name = message.To.OfType<MailboxAddress>().FirstOrDefault().Name,
                            Address = message.To.OfType<MailboxAddress>().FirstOrDefault().Address
                        },
                        Content = message.TextBody,
                        Subject = message.Subject,
                        Attachments = message.Attachments.Select(x => new AttachmentViewModel()
                        {
                            OriginalName = x.ContentDisposition?.FileName ?? x.ContentType.Name,
                            Data = WriteAttachmentToStream(x)
                        })
                    };

                    inspectoMails.Add(newReceivedMessage);
                    await inbox.AddFlagsAsync(uid, MessageFlags.Seen, true).ConfigureAwait(false);
                }

                client.Disconnect(true);
            }
            return inspectoMails;
        }

        /// <summary>
        /// Записывает полученное сообщение в поток
        /// </summary>
        /// <param name="stream"></param>
        /// <param name="attachment"></param>
        private static byte[] WriteAttachmentToStream(MimeEntity attachment)
        {
            var stream = new MemoryStream
            {
                Position = 0
            };

            switch (attachment)
            {
                case MessagePart:
                    {
                        var rfc822 = (MessagePart)attachment;
                        rfc822.Message.WriteTo(stream);
                        break;
                    }

                case MimePart:
                    {
                        var part = (MimePart)attachment;
                        part.Content.DecodeTo(stream);
                        break;
                    }
                default:
                    {
                        throw new ArgumentException("Image not available", nameof(attachment));
                    }
            }

            stream.Position = 0;
            return stream.ToArray();
        }

        /// <summary>
        /// Создаёт новую почту
        /// </summary>
        /// <param name="localPart"></param>
        /// <param name="password"></param>
        /// <param name="name"></param>
        /// <returns></returns>
        public async Task CreateMail(string localPart, string password, string name)
        {
            var httpClient = _httpClientFactory.CreateClient();
            var requestUri = $"http://{_imapServer}/api/v1/add/mailbox";

            httpClient.DefaultRequestHeaders.Accept.Clear();
            httpClient.DefaultRequestHeaders.Add("X-API-Key", _key);
            httpClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

            var requestContent = new StringContent(JsonSerializer.Serialize(new
            {
                active = "1",
                domain = _domain,
                local_part = localPart,
                name = name,
                password = password,
                password2 = password,
                quota = "3072",
                force_pw_update = "0",
                tls_enforce_in = "1",
                tls_enforce_out = "1",
                tags = new string[] {},
            }, _jsonSerializerOptions), Encoding.UTF8, "application/json");

            using var response = await httpClient.PostAsync(requestUri, requestContent);
            var responseJson = await response.Content.ReadAsStringAsync().ConfigureAwait(false);
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine(responseJson);
            Console.ForegroundColor = ConsoleColor.White;
        }
    }
}
