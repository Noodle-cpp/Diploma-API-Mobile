using Data.Models;
using Data.Repositories;
using Domain.Exceptions;
using Domain.ViewModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection.Metadata.Ecma335;
using System.Text;
using System.Threading.Tasks;
using System.Transactions;

namespace Domain.Services
{
    public interface IInspectorScoreService
    {
        public Task CreateScoreAsync(Guid inspectorId, ScoreType type);
        public Task CreateScoreAsync(Guid inspectorId, int score, string comment = "");
        public Task<IEnumerable<InspectorScore>> GetInspectorScoresAsync(Guid inspectorId, 
                                                                            int? page, int? perPage, 
                                                                            DateTime? startDate = null, DateTime? endDate = null);
        public Task<InspectorScore> GetByIdAsync(Guid id);
        public Task<IEnumerable<InspectorRatingScore>> GetInspectorsRatingAsync(Guid inspectorId, DateTime? startDate = null, DateTime? endDate = null);
        public Task<InspectorRatingScore> GetInspectorRatingAsync(Guid inspectorId, DateTime? startDate = null, DateTime? endDate = null);
    }

    public class InspectorScoreService : IInspectorScoreService
    {
        private readonly IInspectorScoreRepository _inspectorScoreRepository;
        private readonly IInspectorRepository _inspectorRepository;

        public InspectorScoreService(IInspectorScoreRepository inspectorScoreRepository,
            IInspectorRepository inspectorRepository)
        {
            _inspectorScoreRepository = inspectorScoreRepository;
            _inspectorRepository = inspectorRepository;
        }

        public async Task<InspectorScore> GetByIdAsync(Guid id)
        {
            return await _inspectorScoreRepository.GetByIdAsync(id).ConfigureAwait(false) ?? throw new ScoreNotFoundException();
        }

        public async Task<IEnumerable<InspectorScore>> GetInspectorScoresAsync(Guid inspectorId, 
                                                                            int? page, int? perPage, 
                                                                            DateTime? startDate = null, DateTime? endDate = null)
        {
            return await _inspectorScoreRepository.GetInspectorScoresAsync(inspectorId, page, perPage, startDate, endDate).ConfigureAwait(false);
        }

        public async Task CreateScoreAsync(Guid inspectorId, ScoreType type)
        {
            int score = (int)type;

            if (type == ScoreType.OverdueIncident || type == ScoreType.Custom)
            {
                var scores = await GetInspectorRatingAsync(inspectorId).ConfigureAwait(false);
                if (scores.TotalScore + score < 0) score -= scores.TotalScore + score;
            }

            if (score == 0) return;

            await _inspectorScoreRepository.CreateInspectorScoreAsync(new InspectorScore
            {
                Id = Guid.NewGuid(),
                Date = DateTime.UtcNow,
                InspectorId = inspectorId,
                Type = type,
                Score = score,
                Comment = ""
            }).ConfigureAwait(false);
        }

        public async Task CreateScoreAsync(Guid inspectorId, int score, string comment = "")
        {
            var scores = await GetInspectorRatingAsync(inspectorId).ConfigureAwait(false);
            if (scores.TotalScore + score < 0) score -= scores.TotalScore + score;

            if (score == 0) return;

            await _inspectorScoreRepository.CreateInspectorScoreAsync(new InspectorScore
            {
                Id = Guid.NewGuid(),
                Date = DateTime.UtcNow,
                InspectorId = inspectorId,
                Type = ScoreType.Custom,
                Score = score,
                Comment = comment
            }).ConfigureAwait(false);
        }

        public async Task<IEnumerable<InspectorRatingScore>> GetInspectorsRatingAsync(Guid inspectorId, DateTime? startDate = null, DateTime? endDate = null)
        {
            var scores = await _inspectorScoreRepository.GetInspectorsScoresAsync(startDate, endDate).ConfigureAwait(false);
            var inspector = await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false) ?? throw new InspectorNotFoundException();

            var leaderboard = new List<InspectorRatingScore>()
            {
                new()
                {
                    Inspector = inspector,
                    InspectorId = inspectorId,
                    Place = 1,
                    TotalScore = 0
                }
            };

            if(scores.Any())
            {
                leaderboard = scores.Select(x => new InspectorRatingScore()
                {
                    InspectorId = x.InspectorId,
                    Inspector = x.Inspector,
                    TotalScore = scores.Where(score => score.InspectorId == x.InspectorId).Sum(score => score.Score)
                })
                .DistinctBy(x => x.InspectorId)
                .OrderByDescending(x => x.TotalScore)
                .Select((x, index) => new InspectorRatingScore()
                {
                    Place = index + 1,
                    InspectorId = x.InspectorId,
                    Inspector = x.Inspector,
                    TotalScore = x.TotalScore,
                }).ToList();
            }

            return leaderboard;
        }

        public async Task<InspectorRatingScore> GetInspectorRatingAsync(Guid inspectorId, DateTime? startDate = null, DateTime? endDate = null)
        {
            var scores = await _inspectorScoreRepository.GetInspectorsScoresAsync(startDate, endDate).ConfigureAwait(false);
            var inspector = await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false) ?? throw new InspectorNotFoundException();
            var rating = new List<InspectorRatingScore>(){
                new()
            {
                Inspector = inspector,
                InspectorId = inspector.Id,
                Place = 1,
                TotalScore = 0
            }
            };

            if (scores.Any())
            {
                rating = scores.Select(x => new InspectorRatingScore()
                {
                    InspectorId = x.InspectorId,
                    Inspector = x.Inspector,
                    TotalScore = scores.Where(score => score.InspectorId == x.InspectorId).Sum(score => score.Score)
                }).DistinctBy(x => x.InspectorId)
                    .OrderByDescending(x => x.TotalScore)
                    .Select((x, index) => new InspectorRatingScore()
                    {
                        Place = index + 1,
                        InspectorId = x.InspectorId,
                        Inspector = x.Inspector,
                        TotalScore = x.TotalScore,
                    }).ToList();
            }

            return rating.FirstOrDefault(x => x.InspectorId == inspectorId) ?? new InspectorRatingScore()
            {
                InspectorId = inspectorId,
                Inspector = inspector,
                Place = rating.Max(x => x.Place) + 1,
                TotalScore = 0
            };
        }
    }
}