using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MailManager.ViewModels
{
    public class AttachmentViewModel
    {
        public byte[] Data { get; set; }

        public string OriginalName { get; set; }

    }
}
