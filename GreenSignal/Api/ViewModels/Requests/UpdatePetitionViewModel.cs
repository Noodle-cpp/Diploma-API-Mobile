using Data.Models;
using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class UpdatePetitionViewModel
    {
        [Required]
        public DateTime Date { get; set; }

        [Required]
        public Guid DepartmentId { get; set; }

        public Guid? IncidentReportId { get; set; }

        public Guid? ParentPetitionId { get; set; }

        [Required]
        public PetitionKind Kind { get; set; }

        [Required]
        public int AttributeVersion { get; set; }
    }
}
