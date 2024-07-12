using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IInspectorSessionRepository
    {
        Task CreateSessionAsync(InspectorSession inspectorSession);
        Task<IEnumerable<InspectorSession>> GetInspectorsNerbyCoordsAsync(double lat, double lng, double maxTimeUpdate, double distanceKm);
        Task RemoveInspectorSession(InspectorSession inspectorSession);
        Task<InspectorSession?> GetInspectorSessionByIdAsync(Guid id);
        Task<IEnumerable<InspectorSession>> GetInspectorSessionsByInspectorIdAsync(Guid inspectorId);
    }

    public class InspectorSessionRepository : IInspectorSessionRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public InspectorSessionRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreateSessionAsync(InspectorSession inspectorSession)
        {
            await _greenSignalContext.InspectorSessions.AddAsync(inspectorSession).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<InspectorSession?> GetInspectorSessionByIdAsync(Guid id)
        {
            return await _greenSignalContext.InspectorSessions.AsNoTracking().FirstOrDefaultAsync(x => x.Id == id).ConfigureAwait(false);
        }

        public async Task<IEnumerable<InspectorSession>> GetInspectorSessionsByInspectorIdAsync(Guid inspectorId)
        {
            return await _greenSignalContext.InspectorSessions.Where(x => x.InspectorId == inspectorId).OrderByDescending(x => x.CretedAt).ToListAsync().ConfigureAwait(false);
        }

        public async Task<IEnumerable<InspectorSession>> GetInspectorsNerbyCoordsAsync(double lat, double lng, double maxTimeUpdate, double distanceKm)
        {
             var sql =   $"SELECT \"InspectorSessions\".* FROM \"InspectorSessions\" " +
                        $"inner join \"Inspectors\" ON \"InspectorSessions\".\"InspectorId\" = \"Inspectors\".\"Id\" " +
                        $"where(earth_box(ll_to_earth({lat.ToString().Replace(',', '.')}, {lng.ToString().Replace(',', '.')}), {distanceKm * 1000}) @> ll_to_earth(\"Inspectors\".\"Lat\", \"Inspectors\".\"Lng\")) " +
                        $"and(earth_distance(ll_to_earth({lat.ToString().Replace(',', '.')}, {lng.ToString().Replace(',', '.')}), ll_to_earth(\"Inspectors\".\"Lat\", \"Inspectors\".\"Lng\")) <= {distanceKm * 1000}) " +
                        $"and \"Inspectors\".\"LastLatLngAt\" >= (now() - '{maxTimeUpdate} hour'::interval)";

            return await _greenSignalContext.InspectorSessions.FromSqlRaw(sql).ToListAsync().ConfigureAwait(false);
        }

        public async Task RemoveInspectorSession(InspectorSession inspectorSession)
        {
            _greenSignalContext.InspectorSessions.Remove(inspectorSession);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait (false);
        }
    }
}
