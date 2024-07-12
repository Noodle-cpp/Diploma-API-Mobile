using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class AttachInspectorTelegramToSystemViewModel
    {
        [Required(ErrorMessage = "Не указан номер телефона")]
        [MaxLength(11)]
        [RegularExpression(@"^(7([0-6]|[8-9])[0-9]{9})$",
            ErrorMessage = "Неверный формат номера")]
        [Display(Name = "Номер телефона")]
        public string Phone { get; set; }

        [Required]
        public string TelegramUserId { get; set; }
        [Required]
        public string ChatId { get; set; }
    }
}
