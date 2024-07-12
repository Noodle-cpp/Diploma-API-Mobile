using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface ISavedFileRepository
    {
        Task CreateSavedFileAsync(SavedFile savedFile);
        Task RemoveSavedFileAsync(SavedFile savedFile);
        Task<SavedFile?> GetSavedFileByIdAsync(Guid id);
    }

    public class SavedFileRepository : ISavedFileRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public SavedFileRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateSavedFileAsync(SavedFile savedFile)
        {
            await _greenSignalContext.SavedFiles.AddAsync(savedFile).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<SavedFile?> GetSavedFileByIdAsync(Guid id)
        {
            return await _greenSignalContext.SavedFiles.FirstOrDefaultAsync(x => x.Id == id).ConfigureAwait(false);
        }

        public async Task RemoveSavedFileAsync(SavedFile savedFile)
        {
            _greenSignalContext.SavedFiles.Remove(savedFile);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }
    }
}
