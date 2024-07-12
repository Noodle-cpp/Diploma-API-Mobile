using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IInspectorRepository
    {
        Task<Inspector?> GetByIdAsync(Guid id);
        Task<Inspector?> GetByPhoneAsync(string phone);
        Task UpdateInspectorAsync(Inspector inspector);
        Task CreateInspectorAsync(Inspector inspector);
        Task<int> GetNextNumberAsync();
        Task<IEnumerable<Inspector>> GetActiveInspectorsAsync();
        Task<Inspector?> GetInspectorByTelegramUserIdAsync(string telegramUserId);
    }

    public class InspectorRepository : IInspectorRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public InspectorRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateInspectorAsync(Inspector inspector)
        {
            await _greenSignalContext.Inspectors.AddAsync(inspector).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<IEnumerable<Inspector>> GetActiveInspectorsAsync()
        {
            return await _greenSignalContext.Inspectors.Where(x => x.ReviewStatus == InspectorStatus.Active)
                                                        .ToListAsync().ConfigureAwait(false);
        }

        public async Task<Inspector?> GetByIdAsync(Guid id)
        {
            return await _greenSignalContext.Inspectors
                                            .AsNoTracking()
                                            .Include(x => x.CertificateFile)
                                            .Include(x => x.PhotoFile)
                                            .Include(x => x.Signature)
                                            .FirstOrDefaultAsync(x => x.Id == id)
                                            .ConfigureAwait(false);
        }

        public async Task<Inspector?> GetByPhoneAsync(string phone)
        {
            return await _greenSignalContext.Inspectors
                                            .AsNoTracking()
                                            .FirstOrDefaultAsync(x => x.Phone == phone)
                                            .ConfigureAwait(false);
        }

        public async Task<Inspector?> GetInspectorByTelegramUserIdAsync(string telegramUserId)
        {
            return await _greenSignalContext.Inspectors.FirstOrDefaultAsync(x => x.TelegramUserId == telegramUserId).ConfigureAwait(false);
        }

        public async Task<int> GetNextNumberAsync()
        {
            var max = await _greenSignalContext.Inspectors.AsNoTracking().MaxAsync(x => (int?)x.Number).ConfigureAwait(false);
            return (max ?? 0) + 1;
        }

        public async Task UpdateInspectorAsync(Inspector inspector)
        {
            _greenSignalContext.Entry(inspector).State = EntityState.Modified;
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }
    }
}
