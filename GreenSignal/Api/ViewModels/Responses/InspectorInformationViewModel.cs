using Data.Models;

namespace Api.ViewModels.Responses
{
    public class InspectorInformationViewModel
    {
        public Guid Id { get; set; }
        public string FIO { get; set; }
        public string Phone { get; set; }
        public InspectorStatus ReviewStatus { get; set; }
        public SavedFileViewModel? PhotoFile { get; set; }
        public string InternalEmail { get; set; }
        public DateTime? LastLatLngAt { get; set; }
        public string CertificateId { get; set; }
        public string SchoolId { get; set; }
        public DateTime? CertificateDate { get; set; }
        public SavedFileViewModel CertificateFile { get; set; }
    }
}
