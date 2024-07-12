using Data.Models;
using System.ComponentModel.DataAnnotations;
using System.Xml.Linq;

namespace Api.ViewModels.Responses
{
    public class InspectorViewModel
    {
        public Guid Id { get; set; }
        public string FIO { get; set; }
        public string Phone { get; set; }
        public InspectorStatus ReviewStatus { get; set; }
        public SavedFileViewModel PhotoFile { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        public int Number { get; set; }
        public string InternalEmail { get; set; }
        public double? Lat { get; set; }
        public double? Lng { get; set; }
        public DateTime? LastLatLngAt { get; set; }
        public string CertificateId { get; set; }
        public string SchoolId { get; set; }
        public DateTime? CertificateDate { get; set; }
        public SavedFileViewModel CertificateFile { get; set; }
        public SavedFileViewModel Signature { get; set; }
    }
}
