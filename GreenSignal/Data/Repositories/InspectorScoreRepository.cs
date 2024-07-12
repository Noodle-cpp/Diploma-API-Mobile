using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IInspectorScoreRepository
    {
        public Task<InspectorScore?> GetByIdAsync(Guid id);
        public Task<IEnumerable<InspectorScore>> GetInspectorsScoresAsync(DateTime? startDate = null, DateTime? endDate = null);
        public Task<IEnumerable<InspectorScore>> GetInspectorScoresAsync(Guid inspectorId, 
                                                                            int? page = null, int? perPage = null, 
                                                                            DateTime? startDate = null, DateTime? endDate = null);
        public Task CreateInspectorScoreAsync(InspectorScore newInspectorScore);
    }

    public class InspectorScoreRepository : IInspectorScoreRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public InspectorScoreRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateInspectorScoreAsync(InspectorScore newInspectorScore)
        {
            await _greenSignalContext.InspectorScores.AddAsync(newInspectorScore).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<InspectorScore?> GetByIdAsync(Guid id)
        {
            return await _greenSignalContext.InspectorScores.Include(x => x.Inspector).FirstOrDefaultAsync(x => x.Id == id).ConfigureAwait(false);
        }

        public async Task<IEnumerable<InspectorScore>> GetInspectorScoresAsync(Guid inspectorId, 
                                                                                int? page = null, int? perPage = null, 
                                                                                DateTime? startDate = null, DateTime? endDate = null)
        {
            var query = _greenSignalContext.InspectorScores.Include(x => x.Inspector)
                                                            .Where(x => (x.InspectorId == inspectorId) &&
                                                                        (startDate == null || x.Date >= startDate) &&
                                                                        (endDate == null || x.Date <= endDate))
                                                            .OrderByDescending(x => x.Date);

            if (page != null && perPage != null)
                query = (IOrderedQueryable<InspectorScore>)query.Skip(((int)page - 1) * (int)perPage).Take((int)perPage);

            return await query.ToListAsync().ConfigureAwait(false);
        }

        public async Task<IEnumerable<InspectorScore>> GetInspectorsScoresAsync(DateTime? startDate = null, DateTime? endDate = null)
        {
            return await _greenSignalContext.InspectorScores.Include(x => x.Inspector)
                                                            .Where(x => (startDate == null || x.Date >= startDate) &&
                                                                        (endDate == null || x.Date <= endDate))
                                                            .OrderByDescending(x => x.Date).ToListAsync().ConfigureAwait(false);
        }
    }
}
