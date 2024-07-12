using Data.Models;
using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class UpdateIncidentReportViewModel
    {
        [Required]
        public DateTime ManualDate { get; set; }

        [Required]
        public IncidentReportKind Kind { get; set; }

        [Required]
        public string Description { get; set; }

        [Required]
        public DateTime StartOfInspection { get; set; }
        [Required]
        public DateTime EndOfInspection { get; set; }

        public Guid? IncidentId { get; set; }

        public Guid? LocationId { get; set; }

        [Required]
        public double Lat { get; set; }
        [Required]
        public double Lng { get; set; }

        [Required]
        public int AttributesVersion { get; set; }
    }
}
