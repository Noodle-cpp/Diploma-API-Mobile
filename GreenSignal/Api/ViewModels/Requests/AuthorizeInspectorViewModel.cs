using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class AuthorizeInspectorViewModel
    {
        [Required]
        [MaxLength(11)]
        [RegularExpression(@"^(7([0-6]|[8-9])[0-9]{9})$",
            ErrorMessage = "Неверный формат номера")]
        public string Phone { get; set; }

        [Required]
        [MaxLength(4)]
        public string Code { get; set; }

        [Required]
        public string DeviceName { get; set; }

        [Required]
        public string FirebaseToken { get; set; }
    }
}
