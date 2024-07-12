using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface ILocationRepository
    {
        Task<Location?> GetLocationByIdAsync(Guid id);
        Task<IEnumerable<Location>> GetLocationsListAsync(int page, int perPage, string title, Guid? parentLocationId);
    }

    public class LocationRepository : ILocationRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public LocationRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task<Location?> GetLocationByIdAsync(Guid id)
        {
            return await _greenSignalContext.Locations.FirstOrDefaultAsync(x => x.Id == id).ConfigureAwait(false);
        }

        public async Task<IEnumerable<Location>> GetLocationsListAsync(int page, int perPage, string title, Guid? parentLocationId)
        {
            return await _greenSignalContext.Locations.AsNoTracking()
                                                        .Where(x => x.Name.ToLower().Contains(title.ToLower()) && x.ParentLocationId == parentLocationId)
                                                        .OrderBy(x => x.Name)
                                                        .Skip((page - 1) * perPage)
                                                        .Take(perPage)
                                                        .ToListAsync().ConfigureAwait(false);
        }
    }
}
