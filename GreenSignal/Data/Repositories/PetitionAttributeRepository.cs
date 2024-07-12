using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IPetitionAttributeRepository
    {
        Task<IEnumerable<PetitionAttribute>> GetListOfAttributesByPetitionIdAsync(Guid id);
        Task RemoveListOfAttributesAsync(IEnumerable<PetitionAttribute> petitionAttributes);
        Task CreateRangeAttributes(IEnumerable<PetitionAttribute> petitionAttributes);

    }

    public class PetitionAttributeRepository : IPetitionAttributeRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public PetitionAttributeRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateRangeAttributes(IEnumerable<PetitionAttribute> petitionAttributes)
        {
            await _greenSignalContext.PetitionAttributes.AddRangeAsync(petitionAttributes).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<IEnumerable<PetitionAttribute>> GetListOfAttributesByPetitionIdAsync(Guid id)
        {
            return await _greenSignalContext.PetitionAttributes.AsNoTracking().Where(x => x.PetitionId == id).ToListAsync().ConfigureAwait(false);
        }

        public async Task RemoveListOfAttributesAsync(IEnumerable<PetitionAttribute> petitionAttributes)
        {
            _greenSignalContext.PetitionAttributes.RemoveRange(petitionAttributes);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }
    }
}
