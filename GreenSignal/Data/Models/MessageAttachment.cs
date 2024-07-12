using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    public class MessageAttachment
    {
        [Key]
        public Guid Id { get; set; }

        [Required]
        public Guid ReceiveMessageId { get; set; }
        public ReceiveMessage ReceiveMessage { get; set; }

        [Required]
        public Guid SavedFileId { get; set; }
        public SavedFile SavedFile { get; set; }
    }
}
