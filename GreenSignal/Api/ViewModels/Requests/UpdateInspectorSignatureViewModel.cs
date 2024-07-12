using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class UpdateInspectorSignatureViewModel
    {
        [Required]
        public IFormFile SignaturePhoto { get; set; }
    }
}
