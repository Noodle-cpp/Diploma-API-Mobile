using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class UpdateInspectorLocation
    {
        [Required]
        public double Lat { get; set; }

        [Required]
        public double Lng { get; set; }
    }
}
