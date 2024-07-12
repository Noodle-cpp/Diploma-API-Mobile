using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IIncidentRepository
    {
        Task<IEnumerable<Incident>> GetIncidentsNerbyCoordsAsync(double maxDistance, double lat, double lng, int page, int perPage, IncidentKind? incidentKind);
        Task<IEnumerable<Incident>> GetIncidentsAsync(int page, int perPage, IncidentKind? incidentKind, IncidentStatus? incidentStatus, Guid? inspectorId);
        Task<Incident?> GetByIdAsync(Guid id);
        Task CreateIncidentAsync(Incident incident);
        Task UpdateIncidentAsync(Incident incident);
        Task<IEnumerable<Incident>> GetIncidentsForCitizenAsync(Guid citizenId, int page, int perPage);
        Task<int> GetIncidentsStatistic(IncidentStatus incidentStatus);
        Task<IEnumerable<Incident>> GetOverdueIncidents(int overdueDays);
    }

    public class IncidentRepository : IIncidentRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public IncidentRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateIncidentAsync(Incident incident)
        {
            await _greenSignalContext.Incidents.AddAsync(incident).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<Incident?> GetByIdAsync(Guid id)
        {
            return await _greenSignalContext.Incidents
                                                        .AsNoTracking()
                                                        .Include(x => x.ReportedBy)
                                                        .Include(x => x.IncidentAttachments)
                                                            .ThenInclude(x => x.SavedFile)
                                                        .AsNoTracking()
                                                        .FirstOrDefaultAsync(x => x.Id == id)
                                                        .ConfigureAwait(false);
        }

        public async Task<IEnumerable<Incident>> GetIncidentsForCitizenAsync(Guid citizenId, int page, int perPage)
        {
            return await _greenSignalContext.Incidents.AsNoTracking()
                                                        .Include(x => x.ReportedBy)
                                                        .Include(x => x.IncidentAttachments)
                                                            .ThenInclude(x => x.SavedFile)
                                                        .Where(x => x.ReportedById == citizenId)
                                                        .OrderBy(x => x.CreatedAt)
                                                        .Skip((page - 1) * perPage)
                                                        .Take(perPage)
                                                        .ToListAsync().ConfigureAwait(false);
        }

        public async Task<int> GetIncidentsStatistic(IncidentStatus incidentStatus)
        {
            return await _greenSignalContext.Incidents.AsNoTracking().Where(x => x.Status == incidentStatus).CountAsync().ConfigureAwait(false);
        }

        public async Task<IEnumerable<Incident>> GetIncidentsNerbyCoordsAsync(double maxDistance, 
                                                                        double lat, double lng, 
                                                                        int page, int perPage, 
                                                                        IncidentKind? incidentKind)
        {
            var sql = $"SELECT * FROM \"Incidents\" " +
                      $"WHERE earth_distance(ll_to_earth({lat.ToString().Replace(',', '.')}, {lng.ToString().Replace(',', '.')}), ll_to_earth(\"Lat\", \"Lng\")) <= {maxDistance * 1000} ";

            return await _greenSignalContext.Incidents.FromSqlRaw(sql)
                                                        .Include(x => x.ReportedBy)
                                                        .Include(x => x.IncidentAttachments)
                                                            .ThenInclude(x => x.SavedFile)
                                                        .Where(x => x.Status == IncidentStatus.Submitted && 
                                                                (incidentKind == null || x.Kind == incidentKind))
                                                        .OrderByDescending(x => x.CreatedAt)
                                                            .ThenBy(x => x.ReportedBy.Rating)
                                                        .Skip((page - 1) * perPage)
                                                        .Take(perPage)
                                                        .ToListAsync().ConfigureAwait(false);
        }

        public async Task<IEnumerable<Incident>> GetOverdueIncidents(int overdueDays)
        {
            return await _greenSignalContext.Incidents
               .Include(x => x.Inspector)
               .Where(x => DateTime.UtcNow.AddDays(-overdueDays) >= x.BindingDate)
               .ToListAsync().ConfigureAwait(false);
        }

        public async Task UpdateIncidentAsync(Incident incident)
        {
            _greenSignalContext.Entry(incident).State = EntityState.Modified;
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<IEnumerable<Incident>> GetIncidentsAsync(int page, int perPage, IncidentKind? incidentKind, IncidentStatus? incidentStatus, Guid? inspectorId)
        {
            return await _greenSignalContext.Incidents
                .Include(x => x.ReportedBy)
                .Include(x => x.IncidentAttachments)
                    .ThenInclude(x => x.SavedFile)
                .Where(x => (incidentKind == null || x.Kind == incidentKind) 
                && (incidentStatus == null || x.Status == incidentStatus)
                && (inspectorId == null || x.InspectorId == inspectorId))
                .OrderByDescending(x => x.CreatedAt)
                    .ThenBy(x => x.ReportedBy.Rating)
                .Skip((page - 1) * perPage)
                .Take(perPage)
                .ToListAsync().ConfigureAwait(false);
        }
    }
}
