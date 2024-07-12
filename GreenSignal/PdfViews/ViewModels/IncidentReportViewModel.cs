using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PdfViews.ViewModels
{
    public class IncidentReportViewModel
    {
        [Display(Name = "Серийный номер")]
        public string SerialNumber { get; set; }

        [Display(Name = "Инспектор")]
        public InspectorViewModel Inspector { get; set; }

        [Display(Name = "Описание")]
        public string Description { get; set; }

        [Display(Name = "Дата оформления")]
        public DateTime ManualDate { get; set; }

        [Display(Name = "Дата начала инспекции")]
        public DateTime StartOfInspection { get; set; }

        [Display(Name = "Дата окончания инспекции")]
        public DateTime EndOfInspection { get; set; }

        [Display(Name = "Нарушение")]
        public IncidentViewModel? Incident { get; set; }

        [Display(Name = "Локация")]
        public LocationViewModel? Location { get; set; }

        [Display(Name = "Статус")]
        public IncidentReportStatus Status { get; set; }
        [Display(Name = "Тип")]
        public IncidentReportKind Kind { get; set; }
        [Display(Name = "Адрес")]
        public string Address { get; set; }
        [Display(Name = "Широта")]
        public double Lat { get; set; }
        [Display(Name = "Долгота")]
        public double Lng { get; set; }

        [Display(Name = "Приложение")]
        public IEnumerable<IncidentReportAttachmentViewModel> IncidentReportAttachements { get; set; }

        [Display(Name = "Поля")]
        public IEnumerable<IncidentReportAttributeViewModel> IncidentReportAttributes { get; set; }
    }

    public enum IncidentReportKind
    {
        [Display(Name = "Загрязнение воздуха")]
        AirPollution,
        [Display(Name = "Загрязнение почвы")]
        SoilPollution,
        [Display(Name = "Раскопки")]
        Excavation,
        [Display(Name = "Свалка")]
        Dump,
        [Display(Name = "Вырубка деревьев")]
        TreeCutting,
        [Display(Name = "Радиация")]
        Radiation
    }
    public enum IncidentReportStatus
    {
        Draft = 0,
        Sent = 100,
        Completed_successfuly = 200,
        Completed_unsucessful = 300,
        Archived = 400
    }
}
