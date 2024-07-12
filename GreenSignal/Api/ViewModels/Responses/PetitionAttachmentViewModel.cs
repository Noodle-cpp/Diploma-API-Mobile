using Data.Models;
using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Responses
{
    public class PetitionAttachmentViewModel
    {
        public Guid Id { get; set; }
        public Guid SavedFileId { get; set; }
        public SavedFileViewModel SavedFile { get; set; }
        public string Description { get; set; }
        public DateTime ManualDate { get; set; }
        public DateTime CreatedAt { get; set; }
    }
}
