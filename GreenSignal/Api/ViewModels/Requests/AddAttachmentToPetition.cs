using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class AddAttachmentToPetition
    {
        [Required]
        public Guid PetitionId { get; set; }

        [Required]
        public IFormFile File { get; set; }

        [Required]
        public string Description { get; set; }
        public double? Lat { get; set; }
        public double? Lng { get; set; }

        [Required]
        public DateTime ManualDate { get; set; }

        [Required]
        public DateTime CreatedAt { get; set; }
    }
}
