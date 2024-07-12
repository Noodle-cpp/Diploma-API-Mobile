using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Responses
{
    public class SavedFileViewModel
    {
        public Guid Id { get; set; }
        public string OrigName { get; set; }
        public string Path { get; set; }
        public DateTime CreatedAt { get; set; }
    }
}
