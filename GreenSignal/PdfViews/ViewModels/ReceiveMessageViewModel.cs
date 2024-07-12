using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PdfViews.ViewModels
{
    public class ReceiveMessageViewModel
    {
        public Guid Id { get; set; }
        public string FromName { get; set; }
        public string FromAddress { get; set; }
        public string Subject { get; set; }
        public string Content { get; set; }
        public bool Seen { get; set; }
        public List<MessageAttachmentViewModel> MessageAttachments { get; set; }
    }
}
