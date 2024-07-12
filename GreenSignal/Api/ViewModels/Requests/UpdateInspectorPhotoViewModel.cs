using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class UpdateInspectorPhotoViewModel
    {
        [Required]
        public IFormFile InspectorPhoto { get; set; }
    }
}
