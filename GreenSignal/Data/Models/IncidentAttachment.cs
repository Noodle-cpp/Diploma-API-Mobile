using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    public class IncidentAttachment
    {
        [Key]
        public Guid Id { get; set; }

        [Required]
        public Guid IncidentId { get; set; }

        [Required]
        public Guid SavedFileId { get; set; }
        public SavedFile SavedFile { get; set; }

        [Required]
        [MaxLength(10000)]
        public string Description { get; set; }
    }
}
