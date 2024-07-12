using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Responses
{
    public class LocationViewModel
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public Guid? ParentLocationId { get; set; }
    }
}
