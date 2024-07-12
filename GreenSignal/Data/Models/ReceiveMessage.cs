using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    public class ReceiveMessage
    {
        [Key]
        public Guid Id { get; set; }

        [MaxLength(100)]
        public string FromName { get; set; }

        [MaxLength(100)]
        public string FromAddress { get; set; }

        [Required]
        public Guid InspectorId { get; set; }
        public Inspector Inspector { get; set; }

        [MaxLength(100)]
        public string Subject { get; set; }

        [Required]
        [MaxLength(10000)]
        public string Content { get; set; }

        [Required]
        public bool Seen { get; set; }

        [Required]
        public DateTime CreatedAt { get; set; }

        public Guid? PetitionId { get; set; }
        public Petition? Petition { get; set; }

        public List<MessageAttachment> MessageAttachments { get; set; }
    }
}
