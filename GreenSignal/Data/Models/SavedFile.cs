using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    public class SavedFile
    {
        [Key]
        public Guid Id { get; set; }

        [Required]
        public string OrigName { get; set; }

        [Required]
        public string Path { get; set; }

        [Required]
        public DateTime CreatedAt { get; set; }

        [Required]
        public SavedFileType Type { get; set; }
    }

    public enum SavedFileType
    {
        Photo,
        File,
        Certificate,
        MessageAttachment
    }
}
