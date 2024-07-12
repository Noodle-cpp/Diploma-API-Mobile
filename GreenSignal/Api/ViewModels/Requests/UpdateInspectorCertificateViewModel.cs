using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class UpdateInspectorCertificateViewModel
    {
        [Required]
        public IFormFile CertificatePhoto { get; set; }
    }
}
