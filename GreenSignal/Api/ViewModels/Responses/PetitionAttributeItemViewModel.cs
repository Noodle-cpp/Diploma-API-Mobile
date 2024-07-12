using Data.Models;

namespace Api.ViewModels.Responses
{
    public class PetitionAttributeItemViewModel
    {
        public string Name { get; set; }
        public string Title { get; set; }
        public string Type { get; set; }
        public int Version { get; set; }
        public bool IsRequired { get; set; }
        public PetitionKind Kind { get; set; }
    }
}
