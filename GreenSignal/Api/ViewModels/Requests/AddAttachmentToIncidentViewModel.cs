using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class AddAttachmentToIncidentViewModel
    {
        [Required]
        public IFormFile File { get; set; }
        public string Description { get; set; }
    }
}
