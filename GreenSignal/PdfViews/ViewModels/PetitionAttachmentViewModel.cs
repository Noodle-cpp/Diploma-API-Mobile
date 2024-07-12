using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PdfViews.ViewModels
{
    public class PetitionAttachmentViewModel
    {
        public Guid Id { get; set; }
        public Guid SavedFileId { get; set; }
        public byte[] SavedFileBytes { get; set; }
        public string Description { get; set; }
        public DateTime ManualDate { get; set; }
        public DateTime CreatedAt { get; set; }
        public Guid PetitionId { get; set; }
    }
}
