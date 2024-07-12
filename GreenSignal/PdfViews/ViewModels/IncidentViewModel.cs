using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PdfViews.ViewModels
{
    public class IncidentViewModel
    {
        [Display(Name = "Описание")]
        public string Description { get; set; }

        [Display(Name = "Адрес")]
        public string Address { get; set; }

        [Display(Name = "Заявитель")]
        public CitizenViewModel ReportedBy { get; set; }

        [Display(Name = "Дата обращения")]
        public DateTime CreatedAt { get; set; }

        [Display(Name = "Статус")]
        public IncidentStatus Status { get; set; }

        [Display(Name = "Тип")]
        public IncidentKind Kind { get; set; }

        [Display(Name = "Дата взятия в работу")]
        public DateTime? BindingDate { get; set; }
    }

    public enum IncidentKind
    {
        [Description("Загрязнение воздуха")]
        AirPollution,
        [Description("Загрязнение почвы")]
        SoilPollution,
        [Description("Добыча недр")]
        Excavation,
        [Description("Свавлка")]
        Dump,
        [Description("Вырубка лесов")]
        TreeCutting,
        [Description("Радиация")]
        Radiation
    }

    public enum IncidentStatus
    {
        [Description("Черновик")]
        Draft,
        [Description("Подтверждёна")]
        Submitted,
        [Description("Прикреплёна")]
        Attached,
        [Description("Завершена")]
        Completed,
        [Description("Закрыта")]
        Closed,
        [Description("Удалена")]
        Deleted
    }
}
