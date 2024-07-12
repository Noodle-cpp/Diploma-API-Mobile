using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PushNotification.ViewModels
{
    public enum MessageType
    {
        DepartmentReply,
        IncidentAround,
        NewReceiveMessage,
        NewSession
    }

    public class NotificationViewModel
    {
        public string Title { get; set; }
        public string Body { get; set; }
        public MessageType MessageType { get; set; }
        public Guid ToId { get; set; }
    }
}
