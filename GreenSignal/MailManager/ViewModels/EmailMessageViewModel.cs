using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Mail;
using System.Text;
using System.Threading.Tasks;

namespace MailManager.ViewModels
{
    public class EmailMessageViewModel
    {
        public EmailAddressViewModel ToAddress { get; set; }

        public EmailAddressViewModel FromAddress { get; set; }
        
        public string Subject { get; set; }
        
        public string Content { get; set; }

        public IEnumerable<AttachmentViewModel> Attachments { get; set; }
    }
}
    