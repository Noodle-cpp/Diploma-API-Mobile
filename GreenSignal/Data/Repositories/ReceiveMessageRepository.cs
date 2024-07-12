using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IReceiveMessageRepository
    {
        Task CreateRangeReceiveMessagesAsync(IEnumerable<ReceiveMessage> receiveMessages);
        Task<ReceiveMessage?> GetReceiveMessageByIdAsync(Guid id);
        Task UpdateReceiveMessageAsync(ReceiveMessage message);
        Task<IEnumerable<ReceiveMessage>> GetInspectorMessagesAsync(Guid inspectorId, string? filter);
    }

    public class ReceiveMessageRepository : IReceiveMessageRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public ReceiveMessageRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateRangeReceiveMessagesAsync(IEnumerable<ReceiveMessage> receiveMessages)
        {
            await _greenSignalContext.ReceiveMessages.AddRangeAsync(receiveMessages).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<IEnumerable<ReceiveMessage>> GetInspectorMessagesAsync(Guid inspectorId, string? filter)
        {
            return await _greenSignalContext.ReceiveMessages.Where(x => x.InspectorId == inspectorId)
                                                            .Include(x => x.MessageAttachments)
                                                                .ThenInclude(x => x.SavedFile)
                                                            .Where(x => filter == null || 
                                                                        x.FromName.ToLower().Contains(filter) ||
                                                                        x.Subject.ToLower().Contains(filter) || 
                                                                        x.Content.ToLower().Contains(filter))
                                                            .OrderByDescending(x => x.CreatedAt).ToListAsync().ConfigureAwait(false);
        }

        public async Task<ReceiveMessage?> GetReceiveMessageByIdAsync(Guid id)
        {
            return await _greenSignalContext.ReceiveMessages.AsNoTracking()
                                                            .Include(x => x.MessageAttachments)
                                                                .ThenInclude(x => x.SavedFile)
                                                            .FirstOrDefaultAsync(x => x.Id == id).ConfigureAwait(false);
        }

        public async Task UpdateReceiveMessageAsync(ReceiveMessage message)
        {
            _greenSignalContext.Entry(message).State = EntityState.Modified;
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }
    }
}