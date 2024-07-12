using Data.Models;
using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Responses
{
    public class IncidentReportViewModel
    {
        public Guid Id { get; set; }
        public string SerialNumber { get; set; }
        public Guid InspectorId { get; set; }
        public InspectorViewModel Inspector { get; set; }
        public string Address { get; set; }
        public string Description { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime ManualDate { get; set; }
        public DateTime StartOfInspection { get; set; }
        public DateTime EndOfInspection { get; set; }
        public Guid? IncidentId { get; set; }
        public IncidentViewModel Incident { get; set; }
        public Guid? LocationId { get; set; }
        public Location? Location { get; set; }
        public IncidentReportStatus Status { get; set; }
        public IncidentReportKind Kind { get; set; }
        public double Lat { get; set; }
        public double Lng { get; set; }
        public int AttributesVersion { get; set; }
        public IEnumerable<IncidentReportAttachmentViewModel> IncidentReportAttachments { get; set; }
        public IEnumerable<IncidentReportAttributeViewModel> IncidentReportAttributes { get; set; }
    }
}
