using Data.Models;
using Data.Repositories;
using Domain.ViewModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Services
{
    public interface ILocationService
    {
        Task<IEnumerable<Location>> GetLocationsList(int page, int perPage, string? title, Guid? parentLocationId);
    }

    public class LocationService : ILocationService
    {
        private readonly ILocationRepository _locationRepository;

        public LocationService(ILocationRepository locationRepository)
        {
            _locationRepository = locationRepository;
        }

        public async Task<IEnumerable<Location>> GetLocationsList(int page, int perPage, string? title, Guid? parentLocationId)
        {
            return await _locationRepository.GetLocationsListAsync(page, perPage, title ?? "", parentLocationId).ConfigureAwait(false);
        }
    }
}
