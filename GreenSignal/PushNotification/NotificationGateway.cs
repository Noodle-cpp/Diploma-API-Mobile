using FirebaseAdmin;
using FirebaseAdmin.Messaging;
using Google.Apis.Auth.OAuth2;
using PushNotification.ViewModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PushNotification
{
    public interface INotificationGateway
    {
        public Task<bool> SendPushNotification(NotificationViewModel notification, string token);
        public Task<bool> SendPushNotification(NotificationViewModel notification, IEnumerable<string> tokens);
    }

    public class NotificationGateway : INotificationGateway
    {
        private readonly FirebaseMessaging messaging;

        public NotificationGateway()
        {
            FirebaseApp.Create(new AppOptions()
            {
                Credential = GoogleCredential.FromFile("key.json")
            });
            messaging = FirebaseMessaging.DefaultInstance;
        }

        /// <summary>
        /// Отправить Push уведомление на одно устройство
        /// </summary>
        /// <param name="notification"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        public async Task<bool> SendPushNotification(NotificationViewModel notification, string token)
        {
            var sendNotification = CreateNotification(notification, token);
            var test = await messaging.SendAsync(sendNotification).ConfigureAwait(false);
            return true;
        }

        /// <summary>
        /// Отправить Push уведомление на несколько устройств
        /// </summary>
        /// <param name="notification"></param>
        /// <param name="tokens"></param>
        /// <returns></returns>
        public async Task<bool> SendPushNotification(NotificationViewModel notification, IEnumerable<string> tokens)
        {
            var sendNotifications = CreateNotification(notification, tokens);
            var result = await messaging.SendEachForMulticastAsync(sendNotifications).ConfigureAwait(false);
            return result.FailureCount == 0;
        }

        /// <summary>
        /// Создать уведомление для устройства
        /// </summary>
        /// <param name="notification"></param>
        /// <param name="token"></param>
        /// <returns></returns>
        private static Message CreateNotification(NotificationViewModel notification, string token)
        {
            var data = SetTypeAndId(notification.MessageType, notification.ToId);
            data["Title"] = notification.Title;
            data["Body"] = notification.Body;
            return new Message()
            {
                Token = token,
                Data = data,
                Notification = new Notification
                {
                    Title = notification.Title,
                    Body = notification.Body,
                }
            };
        }

        /// <summary>
        /// Создать уведомление для нескольких устройств
        /// </summary>
        /// <param name="notification"></param>
        /// <param name="tokens"></param>
        /// <returns></returns>
        private static MulticastMessage CreateNotification(NotificationViewModel notification, IEnumerable<string> tokens)
        {
            var data = SetTypeAndId(notification.MessageType, notification.ToId);
            data["Title"] = notification.Title;
            data["Body"] = notification.Body;
            return new MulticastMessage()
            {
                Tokens = tokens.ToList(),
                Data = data,
                Notification = new Notification
                {
                    Title = notification.Title,
                    Body = notification.Body,
                }
            };
        }

        private static Dictionary<string, string> SetTypeAndId(MessageType type, Guid id)
        {
            Dictionary<string, string> data = new()
            {
                { "Title", "" },
                { "Body", "" }
            };
            switch (type)
            {
                case MessageType.DepartmentReply:
                    data.Add("Type", "DepartmentReply");
                    data.Add("idReceive", id.ToString());
                    break;
                case MessageType.IncidentAround:
                    data.Add("Type", "IncidentAround");
                    data.Add("idIncident", id.ToString());
                    break;
                case MessageType.NewReceiveMessage:
                    data.Add("Type", "NewReceiveMessage");
                    data.Add("idMessage", id.ToString());
                    break;
                default:
                    break;
            }

            return data;
        }
    }
}
