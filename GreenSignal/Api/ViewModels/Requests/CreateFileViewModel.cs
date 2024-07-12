using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class CreateFileViewModel
    {
        [Required]
        public IFormFile File { get; set; }
    }
}
