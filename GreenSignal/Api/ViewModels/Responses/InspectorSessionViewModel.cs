using Data.Models;
using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Responses
{
    public class InspectorSessionViewModel
    {
        public Guid Id { get; set; }
        public string DeviceName { get; set; }
        public string Ip { get; set; }
        public DateTime CretedAt { get; set; }
    }
}
