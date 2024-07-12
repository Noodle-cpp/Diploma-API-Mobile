using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Data.Models
{
    public class IncidentReport
    {
        [Key]
        public Guid Id { get; set; }

        [Required]
        public string SerialNumber { get; set; }

        [Required]
        public string Description { get; set; }

        [Required]
        public Guid InspectorId { get; set; }
        public Inspector Inspector { get; set; }

        [Required]
        //Дата создания акта
        public DateTime CreatedAt { get; set; }

        [Required]
        //Дата оформления акта
        public DateTime ManualDate { get; set; }

        [Required]
        public DateTime StartOfInspection { get; set; }

        [Required]
        public DateTime EndOfInspection { get; set; }

        public Guid? IncidentId { get; set; }
        public Incident? Incident { get; set; }

        public Guid? LocationId { get; set; }
        public Location? Location { get; set; }

        [Required]
        public IncidentReportStatus Status { get; set; }

        [Required]
        public IncidentReportKind Kind { get; set; }

        [Required]
        public string Address { get; set; }
        
        [Required]
        public double Lat { get; set; }
        [Required]
        public double Lng { get; set; }

        [Required]
        public int AttributesVersion { get; set; }

        public IEnumerable<IncidentReportAttachment> IncidentReportAttachments { get; set; }
        public IEnumerable<IncidentReportAttribute> IncidentReportAttributes { get; set; }
    }


    public enum IncidentReportKind
    {
        [Display(Name = "Загрязнение воздуха")]
        AirPollution,
        [Display(Name = "Загрязнение почвы")]
        SoilPollution,
        [Display(Name = "Добыча недр")]
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
