using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class AuthorizeCitizenViewModel
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
        [MaxLength(255)]
        public string FIO { get; set; }
    }
}
