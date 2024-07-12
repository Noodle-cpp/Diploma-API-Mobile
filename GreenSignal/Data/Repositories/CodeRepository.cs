using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface ICodeRepository
    {
        Task<Code> GetByPhoneAsync(string phone);
        Task CreateCodeAsync(Code code);
        Task UpdateCodeAsync(Code code);
        Task RemoveCodeAsync(Code code);
    }

    public class CodeRepository : ICodeRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public CodeRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateCodeAsync(Code code)
        {
            await _greenSignalContext.Codes.AddAsync(code).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<Code?> GetByPhoneAsync(string phone)
        {
            return await _greenSignalContext.Codes.AsNoTracking().FirstOrDefaultAsync(x => x.Phone == phone).ConfigureAwait(false);
        }

        public async Task RemoveCodeAsync(Code code)
        {
            _greenSignalContext.Codes.Remove(code);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task UpdateCodeAsync(Code code)
        {
            _greenSignalContext.Entry(code).State = EntityState.Modified;
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }
    }
}
