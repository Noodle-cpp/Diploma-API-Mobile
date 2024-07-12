using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    public class Code
    {
        [Key]
        [Display(Name = "Идентификатор")]
        public Guid Id { get; set; }

        [Required]
        [MaxLength(4)]
        [Display(Name = "Код доступа")]
        public string AccessCode { get; set; }

        [Required]
        [MaxLength(11)]
        [RegularExpression(@"^(7([0-6]|[8-9])[0-9]{9})$",
            ErrorMessage = "Неверный формат номера")]
        [Display(Name = "Номер телефона")]
        public string Phone { get; set; }

        [Required]
        [Display(Name = "Дата первого запроса")]
        public DateTime CreatedAt { get; set; }

        [Required]
        [Display(Name = "Дата последнего запроса")]
        public DateTime DateLastRenewal { get; set; }

        [Required]
        [Display(Name = "Количество запросов")]
        public int CountOfRequests { get; set; }
    }
}
