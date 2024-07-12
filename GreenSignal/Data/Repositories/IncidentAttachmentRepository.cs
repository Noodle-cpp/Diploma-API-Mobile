using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IIncidentAttachmentRepository
    {
        Task CreateIncidentAttachmentAsync(IncidentAttachment incidentAttachment);
        Task RemoveIncidentAttachmentAsync(IncidentAttachment incidentAttachment);
        Task<IncidentAttachment?> GetIncidentAttachmentByIdAsync(Guid id);
    }

    public class IncidentAttachmentRepository : IIncidentAttachmentRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public IncidentAttachmentRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateIncidentAttachmentAsync(IncidentAttachment incidentAttachment)
        {
            await _greenSignalContext.AddAsync(incidentAttachment).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<IncidentAttachment?> GetIncidentAttachmentByIdAsync(Guid id)
        {
            return await _greenSignalContext.IncidentAttachments.FirstOrDefaultAsync(x => x.Id == id).ConfigureAwait(false);
        }

        public async Task RemoveIncidentAttachmentAsync(IncidentAttachment incidentAttachment)
        {
            _greenSignalContext.IncidentAttachments.Remove(incidentAttachment);
            await _greenSignalContext.SaveChangesAsync();
        }
    }
}
