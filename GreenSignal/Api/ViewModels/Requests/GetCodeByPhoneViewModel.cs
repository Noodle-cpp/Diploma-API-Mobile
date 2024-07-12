using System.ComponentModel.DataAnnotations;
using System.Xml.Linq;

namespace Api.ViewModels.Requests
{
    public class GetCodeByPhoneViewModel
    {
        [Required]
        [MaxLength(11)]
        [RegularExpression(@"^(7([0-6]|[8-9])[0-9]{9})$",
            ErrorMessage = "Неверный формат номера")]
        public string Phone { get; set; }
    }
}
