using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class CreateIncidentReportAttributeViewModel
    {
        [Required]
        public string Name { get; set; }
        public string? StringValue { get; set; }
        public double? NumberValue { get; set; }
        public bool? BoolValue { get; set; }
    }
}
