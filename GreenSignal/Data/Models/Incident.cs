using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Reflection.PortableExecutable;

namespace Data.Models
{
    public class Incident
    {
        [Key]
        public Guid Id { get; set; }

        [MaxLength(10000)]
        public string Description { get; set; }

        [MaxLength(10000)]
        public string Address { get; set; }

        public double? Lat { get; set; }

        public double? Lng { get; set; }

        public Guid ReportedById { get; set; }
        public Citizen ReportedBy { get; set; }

        [Required]
        public DateTime CreatedAt { get; set; }

        public DateTime? UpdatedAt { get; set; }

        [Required]
        public IncidentStatus Status { get; set; }

        [Required]
        public IncidentKind Kind { get; set; }

        //Дата взятия в работу
        public DateTime? BindingDate { get; set; }

        public Guid? InspectorId { get; set; }
        public Inspector? Inspector { get; set; }

        public IEnumerable<IncidentAttachment> IncidentAttachments { get; set; }
    }

    public enum IncidentKind
    {
        [Description("Загрязнение воздуха")]
        AirPollution,
        [Description("Загрязнение почвы")]
        SoilPollution,
        [Description("Добыча недр")]
        Excavation,
        [Description("Свавлка")]
        Dump,
        [Description("Вырубка лесов")]
        TreeCutting,
        [Description("Радиация")]
        Radiation
    }

    public enum IncidentStatus
    {
        [Description("Черновик")]
        Draft,
        [Description("Подтверждёна")]
        Submitted,
        [Description("Прикреплёна")]
        Attached,
        [Description("Завершена")]
        Completed,
        [Description("Закрыта")]
        Closed,
        [Description("Удалена")]
        Deleted
    }

    public enum ReportType
    {
        [Description("Нерешаемая")]
        Unsolvable,

        [Description("Нерешаемая")]
        Unacceptable
    }
}
