using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface ICitizenRepository
    {
        Task<Citizen?> GetByIdAsync(Guid id);
        Task<Citizen?> GetByPhoneAsync(string phone);
        Task CreateCitizenAsync(Citizen citizen);
        Task UpdateCitizenAsync(Citizen citizen);
        Task<Citizen?> GetCitizenByTelegramUserId(string telegramUserId);
    }

    public class CitizenRepository : ICitizenRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public CitizenRepository( GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateCitizenAsync(Citizen citizen)
        {
            await _greenSignalContext.Citizens.AddAsync(citizen).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<Citizen?> GetByIdAsync(Guid id)
        {
            return await _greenSignalContext.Citizens
                                            .AsNoTracking()
                                            .FirstOrDefaultAsync(x => x.Id == id)
                                            .ConfigureAwait(false);
        }

        public async Task<Citizen?> GetByPhoneAsync(string phone)
        {
            return await _greenSignalContext.Citizens
                                            .AsNoTracking()
                                            .FirstOrDefaultAsync(x => x.Phone == phone)
                                            .ConfigureAwait(false);
        }

        public async Task<Citizen?> GetCitizenByTelegramUserId(string telegramUserId)
        {
            return await _greenSignalContext.Citizens.FirstOrDefaultAsync(x => x.TelegramUserId == telegramUserId).ConfigureAwait(false);
        }

        public async Task UpdateCitizenAsync(Citizen citizen)
        {
            _greenSignalContext.Entry(citizen).State = EntityState.Modified;
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }
    }
}
