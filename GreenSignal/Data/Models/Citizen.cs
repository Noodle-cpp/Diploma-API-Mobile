using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Data.Models
{
    public class Citizen
    {
        [Key]
        [Display(Name = "Идентификатор")]
        public Guid Id { get; set; }

        [Required(ErrorMessage = "Не указан номер телефона")]
        [MaxLength(11)]
        [RegularExpression(@"^(7([0-6]|[8-9])[0-9]{9})$",
            ErrorMessage = "Неверный формат номера")]
        [Display(Name = "Номер телефона")]
        public string Phone { get; set; }

        [Required(ErrorMessage = "Не указано имя")]
        [MaxLength(70)]
        [Display(Name = "Ф.И.О.")]
        public string FIO { get; set; }

        [DefaultValue(10)]
        [Required]
        public double Rating { get; set; }

        public string? TelegramUserId { get; set; }
    }
}
