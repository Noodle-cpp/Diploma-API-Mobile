using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    public   class Petition
    {
        [Key]
        public Guid Id { get; set; }

        [Required]
        [MaxLength(100)]
        public string SerialNumber { get; set; }

        [Required]
        public DateTime Date { get; set; }

        [Required]
        public DateTime CreatedAt { get; set; }

        [Required]
        public Guid DepartmentId { get; set; }
        public Department Department { get; set; }

        public Guid? IncidentReportId { get; set; }
        public IncidentReport? IncidentReport { get; set; }

        public Guid? ParentPetitionId { get; set; }
        public Petition? ParentPetition { get; set; }

        [Required]
        public Guid InspectorId { get; set; }
        public Inspector Inspector { get; set; }

        [Required]
        public PetitionStatus Status { get; set; }

        [Required]
        public PetitionKind Kind { get; set; }

        [Required]
        public int AttributeVersion { get; set; }

        public IEnumerable<PetitionAttachment> Attachments { get; set; }
        public IEnumerable<PetitionAttribute> Attributes { get; set; }
        public IEnumerable<ReceiveMessage> ReceiveMessages { get; set; }
    }

    public enum PetitionKind
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
