using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IIncidentReportAttributeRepository
    {
        Task<IEnumerable<IncidentReportAttribute>> GetListOfAttributesByIncidentReportIdAsync(Guid incidentReportId);
        Task RemoveListOfAttributes(IEnumerable<IncidentReportAttribute> incidentReportAttributes);
        Task CreateRangeAttributes(IEnumerable<IncidentReportAttribute> reportAttributes);
    }

    public class IncidentReportAttributeRepository : IIncidentReportAttributeRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public IncidentReportAttributeRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateRangeAttributes(IEnumerable<IncidentReportAttribute> reportAttributes)
        {
            await _greenSignalContext.IncidentReportAttributes.AddRangeAsync(reportAttributes).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<IEnumerable<IncidentReportAttribute>> GetListOfAttributesByIncidentReportIdAsync(Guid incidentReportId)
        {
            return await _greenSignalContext.IncidentReportAttributes.Where(x => x.IncidentReportId == incidentReportId).ToListAsync().ConfigureAwait(false);
        }

        public async Task RemoveListOfAttributes(IEnumerable<IncidentReportAttribute> incidentReportAttributes)
        {
            _greenSignalContext.IncidentReportAttributes.RemoveRange(incidentReportAttributes);
            await _greenSignalContext.SaveChangesAsync();
        }
    }
}
