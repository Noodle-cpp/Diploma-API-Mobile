using Data.Models;

namespace Api.ViewModels.Responses
{
    public class InspectorScoreViewModel
    {
        public Guid Id { get; set; }
        public int Score { get; set; }
        public DateTime Date { get; set; }
        public ScoreType Type { get; set; }
        public string Comment { get; set; }
    }
}
