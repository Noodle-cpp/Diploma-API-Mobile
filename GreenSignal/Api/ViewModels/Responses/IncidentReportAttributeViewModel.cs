using Data.Models;
using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Responses
{
    public class IncidentReportAttributeViewModel
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public string? StringValue { get; set; }
        public double? NumberValue { get; set; }
        public bool? BoolValue { get; set; }
    }
}
