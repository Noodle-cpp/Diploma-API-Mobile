using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    public class PetitionAttachment
    {
        [Key]
        public Guid Id { get; set; }

        [Required]
        public Guid SavedFileId { get; set; }

        [Required]
        public SavedFile SavedFile { get; set; }

        [Required]
        [MaxLength(10000)]
        public string Description { get; set; }

        [Required]
        public DateTime ManualDate { get; set; }

        [Required]
        public DateTime CreatedAt { get; set; }

        [Required]
        public Guid PetitionId { get; set; }
        public Petition Petition { get; set; }
    }
}
