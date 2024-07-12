using Data.Models;

namespace Api.ViewModels.Responses
{
    public class ReceiveMessageViewModel
    {
        public Guid Id { get; set; }
        public string FromName { get; set; }
        public string FromAddress { get; set; }
        public string Subject { get; set; }
        public string Content { get; set; }
        public DateTime CreatedAt { get; set; }
        public bool Seen { get; set; }
        public Guid? PetitionId { get; set; }
        public IEnumerable<MessageAttachmentViewModel> MessageAttachments { get; set; }
    }
}
