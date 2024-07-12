using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PdfViews.ViewModels
{
    public class PetitionViewModel
    {
        public Guid Id { get; set; }
        public string SerialNumber { get; set; }
        public DateTime Date { get; set; }
        public Guid DepartmentId { get; set; }
        public DepartmentViewModel Department { get; set; }
        public Guid? IncidentReportId { get; set; }
        public IncidentReportViewModel IncidentReport { get; set; }
        public Guid InspectorId { get; set; }
        public InspectorViewModel Inspector { get; set; }
        public PetitionStatus Status { get; set; }
        public PetitionKind Kind { get; set; }
        public int AttributeVersion { get; set; }
        public IEnumerable<PetitionAttachmentViewModel> Attachements { get; set; }
        public IEnumerable<PetitionAttributeViewModel> Attributes { get; set; }
        public IEnumerable<ReceiveMessageViewModel> ReceiveMessages { get; set; }
    }

    public enum PetitionKind
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

    public enum PetitionStatus
    {
        Draft = 0,
        Sent = 100,
        Replied = 200,
        Success = 300,
        Failed = 500,
        Archived = 600
    }
}
