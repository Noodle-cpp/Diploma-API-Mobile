namespace Api.ViewModels.Responses
{
    public class IncidentAttachmentViewModel
    {
        public Guid Id { get; set; }
        public Guid SavedFileId { get; set; }
        public SavedFileViewModel SavedFile { get; set; }
        public string Description { get; set; }
    }
}
