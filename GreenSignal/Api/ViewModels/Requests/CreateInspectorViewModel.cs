using Microsoft.AspNetCore.Mvc;
using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class CreateInspectorViewModel
    {
        [Required]
        [MaxLength(255)]
        public string FIO { get; set; }

        [Required]
        [MaxLength(11)]
        [RegularExpression(@"^(7([0-6]|[8-9])[0-9]{9})$",
            ErrorMessage = "Неверный формат номера")]
        public string Phone { get; set; }

        [Required]
        [MaxLength(100)]
        [RegularExpression(@"([0-9]{3})-([0-9]{4})",
            ErrorMessage = "Неверный формат сертификата")]
        public string CertificateId { get; set; }

        public DateTime? CertificateDate { get; set; }

        [Required]
        [RegularExpression(@"([0-9]{3})-([0-9]{5})-([0-9]{2})",
            ErrorMessage = "Неверный формат аттестата")]
        public string SchoolId { get; set; }

        [Required]
        public IFormFile InspectorPhoto { get; set; }

        [Required]
        public IFormFile CertificatePhoto { get; set; }

        [Required]
        [MaxLength(4)]
        public string Code { get; set; }

        [Required]
        public string FirebaseToken { get; set; }

        [Required]
        public string DeviceName { get; set; }
    }
}
