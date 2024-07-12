using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IIncidentComplaintRepository
    {
        Task<IncidentComplaint?> GetIncidentComplaintByInspectorIdAsync(Guid incidentId, Guid inspectorId);
        Task<int> GetCountOfIncidentComplaintAsync(Guid incidentId);
        Task CreateIncidentComplaintAsync(IncidentComplaint incidentComplaint);
    }

    public class IncidentComplaintRepository : IIncidentComplaintRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public IncidentComplaintRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateIncidentComplaintAsync(IncidentComplaint incidentComplaint)
        {
            await _greenSignalContext.IncidentComplaints.AddAsync(incidentComplaint).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<int> GetCountOfIncidentComplaintAsync(Guid incidentId)
        {
            return await _greenSignalContext.IncidentComplaints.CountAsync(x => x.IncidentId == incidentId).ConfigureAwait(false);
        }

        public async Task<IncidentComplaint?> GetIncidentComplaintByInspectorIdAsync(Guid incidentId, Guid inspectorId)
        {
            return await _greenSignalContext.IncidentComplaints
                .FirstOrDefaultAsync(x => incidentId == x.IncidentId && inspectorId == x.InspectorId).ConfigureAwait(false);
        }
    }
}
