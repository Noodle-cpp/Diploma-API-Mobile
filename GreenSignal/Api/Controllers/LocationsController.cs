using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;
using Domain.Services;
using Infrastructure;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;

namespace Api.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class LocationsController : ControllerBase
    {
        private readonly ILocationService _locationService;
        private readonly IMapper _mapper;
        private readonly IInspectorService _inspectorService;
        private readonly ICitizenService _citizenService;
        private readonly string _key;

        public LocationsController(ILocationService locationService,
                                    IMapper mapper,
                                    IInspectorService inspectorService,
                                    ICitizenService citizenService,
                                    IOptions<GreenSignalConfigurationOptions> options)
        {
            _locationService = locationService;
            _mapper = mapper;
            _inspectorService = inspectorService;
            _citizenService = citizenService;
            _key = options.Value.ApiKey;
        }

        /// <summary>
        /// Получает список городов и областей
        /// </summary>
        /// <param name="page">Страница</param>
        /// <param name="perPage">Кол-во на одной странице</param>
        /// <param name="title">Название</param>
        /// <param name="parentLocationId">Родительская локация</param>
        /// <returns></returns>
        /// <response code="200"></response>
        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<LocationViewModel>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [AuthorizeCitizen]
        public async Task<IActionResult> GetLocationsAsync([FromQuery] int page,
                                                           [FromQuery] int perPage,
                                                           [FromQuery] string? title,
                                                           [FromQuery] Guid? parentLocationId)
        {
            try
            {
                var citizen = (Citizen)HttpContext.Items["User"];
                if (citizen == null)
                {
                    if (!HttpContext.Request.Headers.TryGetValue("ApiKey", out var apiKey))
                        if (String.IsNullOrEmpty(apiKey) && apiKey.First()?.Split('@').First() == _key)
                            return Unauthorized();
                    citizen = await _citizenService.GetCitizenByTelegramUserIdAsync(apiKey.First().Split('@').Last()).ConfigureAwait(false);
                    if (citizen == null)
                        return Unauthorized();
                }

                var locations = await _locationService.GetLocationsList(page, perPage, title, parentLocationId).ConfigureAwait(false);
                return Ok(_mapper.Map<IEnumerable<LocationViewModel>>(locations));
            }
            catch (Exception)
            {
                throw;
            }
        }
    }
}
