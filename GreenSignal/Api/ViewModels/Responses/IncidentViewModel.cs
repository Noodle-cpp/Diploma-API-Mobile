using Data.Models;
using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Responses
{
    public class IncidentViewModel
    {
        public Guid Id { get; set; }
        public string Description { get; set; }
        public string Address { get; set; }
        public double? Lat { get; set; }
        public double? Lng { get; set; }
        public Guid ReportedById { get; set; }
        public CitizenViewModel ReportedBy { get; set; }
        public DateTime CreatedAt { get; set; }
        public IncidentStatus Status { get; set; }
        public IncidentKind Kind { get; set; }
        public DateTime? BindingDate { get; set; }
        public Guid? InspectorId { get; set; }
        public InspectorForIncidentViewModel? Inspector { get; set; }
        public IEnumerable<IncidentAttachmentViewModel> IncidentAttachments { get; set; }
    }
}
