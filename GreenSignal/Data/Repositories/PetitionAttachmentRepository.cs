using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IPetitionAttachmentRepository
    {
        Task CreatePetitionAttachmentAsync(PetitionAttachment petitionAttachment);
        Task<PetitionAttachment?> GetPetitionAttachmentByIdAsync(Guid id);
        Task RemovePetitionAttachmentAsync(PetitionAttachment petition);
    }

    public class PetitionAttachmentRepository : IPetitionAttachmentRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public PetitionAttachmentRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreatePetitionAttachmentAsync(PetitionAttachment petitionAttachment)
        {
            await _greenSignalContext.PetitionAttachments.AddAsync(petitionAttachment).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<PetitionAttachment?> GetPetitionAttachmentByIdAsync(Guid id)
        {
            return await _greenSignalContext.PetitionAttachments.AsNoTracking()
                                                                .FirstOrDefaultAsync(x => x.Id == id).ConfigureAwait(false);
        }

        public async Task RemovePetitionAttachmentAsync(PetitionAttachment petition)
        {
            _greenSignalContext.PetitionAttachments.Remove(petition);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }
    }
}
