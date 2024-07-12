using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class UpdateIncidentReportAttachmentViewModel
    {

        [Required]
        public string Description { get; set; }

        [Required]
        public DateTime ManualDate { get; set; }
    }
}
