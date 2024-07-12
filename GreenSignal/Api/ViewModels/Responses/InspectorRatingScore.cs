using Data.Models;

namespace Api.ViewModels.Responses
{
    public class InspectorRatingScore
    {
        public int Place { get; set; }
        public Guid InspectorId { get; set; }
        public InspectorViewModel Inspector { get; set; }
        public int TotalScore { get; set; }
    }
}
