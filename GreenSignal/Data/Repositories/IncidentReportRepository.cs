using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IIncidentReportRepository
    {
        Task<IEnumerable<IncidentReport>> GetListAsync(Guid inspectorId, int page, int perPage, IncidentReportKind? incidentReportKind, IncidentReportStatus? incidentReportStatus);
        Task<IncidentReport?> GetIncidentReportByIdAsync(Guid id);
        Task CreateIncidentReportAsync(IncidentReport incidentReport);
        Task UpdateIncidentReportAsync(IncidentReport incidentReport);
        Task<int> GetCountOfIncidentReportsByInspectorIdAsync(Guid inspectorId);
        Task<bool> IsIncidentReportAttached(Guid id);
        Task<int> GetSentIncidentReportCount();
    }

    public class IncidentReportRepository : IIncidentReportRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public IncidentReportRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateIncidentReportAsync(IncidentReport incidentReport)
        {
            await _greenSignalContext.IncidentReports.AddAsync(incidentReport).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<IncidentReport?> GetIncidentReportByIdAsync(Guid id)
        {
            return await _greenSignalContext.IncidentReports
                                                            .AsNoTracking()
                                                            .Include(x => x.IncidentReportAttachments)
                                                                .ThenInclude(x => x.SavedFile)
                                                        .AsNoTracking()
                                                            .Include(x => x.IncidentReportAttributes)
                                                        .AsNoTracking()
                                                            .Include(x => x.Inspector)
                                                                .ThenInclude(x => x.CertificateFile)
                                                        .AsNoTracking()
                                                            .Include(x => x.Inspector)
                                                                .ThenInclude(x => x.PhotoFile)
                                                        .AsNoTracking()
                                                            .Include(x => x.Location)
                                                        .AsNoTracking()
                                                            .Include(x => x.Incident)
                                                        .AsNoTracking()
                                                            .Include(x => x.Incident)
                                                                .ThenInclude(x => x.ReportedBy)
                                                        .AsNoTracking()
                                                            .FirstOrDefaultAsync(x => x.Id == id)
                                                            .ConfigureAwait(false);
        }

        public async Task<int> GetCountOfIncidentReportsByInspectorIdAsync(Guid inspectorId)
        {
            return await _greenSignalContext.IncidentReports.CountAsync(x => x.InspectorId == inspectorId).ConfigureAwait(false);
        }

        public async Task<IEnumerable<IncidentReport>> GetListAsync(Guid inspectorId, int page, int perPage, 
                                                                    IncidentReportKind? incidentReportKind, IncidentReportStatus? incidentReportStatus)
        {
            return await _greenSignalContext.IncidentReports.Include(x => x.IncidentReportAttachments)
                                                                .ThenInclude(x => x.SavedFile)
                                                            .Include(x => x.IncidentReportAttributes)
                                                            .Include(x => x.Inspector)
                                                                .ThenInclude(x => x.CertificateFile)
                                                            .Include(x => x.Inspector)
                                                                .ThenInclude(x => x.PhotoFile)
                                                            .Include(x => x.Location)
                                                            .Include(x => x.Incident)
                                                                .ThenInclude(x => x.ReportedBy)
                                                            .Where(x => x.InspectorId == inspectorId
                                                            && x.Status != IncidentReportStatus.Archived
                                                            && (incidentReportKind == null || x.Kind == incidentReportKind)
                                                            && (incidentReportStatus == null || x.Status == incidentReportStatus))
                                                            .OrderByDescending(x => x.CreatedAt)
                                                            .Skip((page - 1) * perPage)
                                                            .Take(perPage)
                                                            .ToListAsync().ConfigureAwait(false);
        }

        public async Task UpdateIncidentReportAsync(IncidentReport incidentReport)
        {
            _greenSignalContext.Entry(incidentReport).State = EntityState.Modified;
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<bool> IsIncidentReportAttached(Guid id)
        {
            return await _greenSignalContext.Petitions.AnyAsync(x => x.IncidentReportId == id && x.Status != PetitionStatus.Archived).ConfigureAwait(false);
        }

        public async Task<int> GetSentIncidentReportCount()
        {
            return await _greenSignalContext.IncidentReports.AsNoTracking().Where(x => x.Status == IncidentReportStatus.Sent).CountAsync().ConfigureAwait(false);
        }
    }
}
