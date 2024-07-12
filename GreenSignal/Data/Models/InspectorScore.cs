using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    public enum ScoreType
    {
        [Display(Name = "Взятие нарушения")]
        TakeIncident = 100,
        [Display(Name = "Отправление обращения")]
        PetitionSent = 150,
        [Display(Name = "Закрытие обращения")]
        PetitionClose = 200,
        [Display(Name = "Просроченное нарушение")]
        OverdueIncident = -150,
        [Display(Name = "Пользовательский")]
        Custom = 0
    }

    public class InspectorScore
    {
        [Key]
        [DisplayName("Идентификатор")]
        public Guid Id { get; set; }

        [Required]
        [DisplayName("Инспектор Id")]
        public Guid InspectorId { get; set; }

        [Required]
        [DisplayName("Инспектор")]
        public Inspector Inspector { get; set; }

        [Required]
        [DisplayName("Очки")]
        public int Score { get; set; } = 0;

        [Required]
        [DisplayName("Дата начисления")]
        public DateTime Date { get; set; }

        [Required]
        [MaxLength(100)]
        [Column(TypeName = "character varying(100)")]
        [DisplayName("Тип")]
        public ScoreType Type { get; set; }

        [MaxLength(1000)]
        [DisplayName("Комментарий")]
        public string Comment { get; set; }
    }
}
