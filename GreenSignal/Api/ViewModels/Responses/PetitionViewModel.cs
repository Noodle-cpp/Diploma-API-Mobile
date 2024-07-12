using Data.Models;
using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Responses
{
    public class PetitionViewModel
    {
        public Guid Id { get; set; }
        public string SerialNumber { get; set; }
        public DateTime Date { get; set; }
        public DateTime CreatedAt { get; set; }
        public Guid DepartmentId { get; set; }
        public DepartmentViewModel Department { get; set; }
        public Guid? IncidentReportId { get; set; }
        public IncidentReportViewModel IncidentReport { get; set; }
        public Guid? ParentPetitionId { get; set; }
        public PetitionViewModel ParentPetition { get; set; }
        public Guid InspectorId { get; set; }
        public InspectorViewModel Inspector { get; set; }
        public PetitionStatus Status { get; set; }
        public PetitionKind Kind { get; set; }
        public int AttributeVersion { get; set; }
        public IEnumerable<PetitionAttachmentViewModel> Attachments { get; set; }
        public IEnumerable<PetitionAttributeViewModel> Attributes { get; set; }
        public IEnumerable<ReceiveMessageViewModel> ReceiveMessages { get; set; }
    }
}
