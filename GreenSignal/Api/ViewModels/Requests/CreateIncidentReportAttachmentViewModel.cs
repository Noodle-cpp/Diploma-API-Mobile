using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class CreateIncidentReportAttachmentViewModel
    {
        [Required]
        public IFormFile File { get; set; }

        [Required]
        public string Description { get; set; }

        [Required]
        public DateTime ManualDate { get; set; }
    }
}
