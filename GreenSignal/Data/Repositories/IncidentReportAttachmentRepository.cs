using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IIncidentReportAttachmentRepository
    {
        Task CreateIncidentReportAttachmentAsync(IncidentReportAttachment incidentReport);
        Task<IncidentReportAttachment?> GetIncidentReportAttachmentByIdAsync(Guid id);
        Task RemoveIncidentReportAttachmentAsync(IncidentReportAttachment incidentReportAttachment);
        Task UpdateIncidentReportAttachment(IncidentReportAttachment incidentReportAttachment);
    }

    public class IncidentReportAttachmentRepository : IIncidentReportAttachmentRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public IncidentReportAttachmentRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateIncidentReportAttachmentAsync(IncidentReportAttachment incidentReport)
        {
            await _greenSignalContext.IncidentReportAttachments.AddAsync(incidentReport).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<IncidentReportAttachment?> GetIncidentReportAttachmentByIdAsync(Guid id)
        {
            return await _greenSignalContext.IncidentReportAttachments.FirstOrDefaultAsync(x => x.Id == id).ConfigureAwait(false);
        }

        public async Task RemoveIncidentReportAttachmentAsync(IncidentReportAttachment incidentReportAttachment)
        {
            _greenSignalContext.IncidentReportAttachments.Remove(incidentReportAttachment);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task UpdateIncidentReportAttachment(IncidentReportAttachment incidentReportAttachment)
        {
            _greenSignalContext.Entry(incidentReportAttachment).State = EntityState.Modified;
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }
    }
}
