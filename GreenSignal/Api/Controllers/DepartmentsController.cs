using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;
using Domain.Exceptions;
using Domain.Services;
using Infrastructure;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;

namespace Api.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class DepartmentsController : ControllerBase
    {
        private readonly IAuthenticationService _authenticationService;
        private readonly IInspectorService _inspectorService;
        private readonly IMapper _mapper;
        private readonly string _key;
        private readonly IDepartmentService _departmentService;
        private readonly IInspectorSessionService _inspectorSessionService;

        public DepartmentsController(IAuthenticationService authenticationService,
                                        IMapper mapper,
                                        IOptions<GreenSignalConfigurationOptions> options,
                                        IInspectorService inspectorService,
                                        IDepartmentService departmentService,
                                        IInspectorSessionService inspectorSessionService)
        {
            _authenticationService = authenticationService;
            _key = options.Value.ApiKey;
            _mapper = mapper;
            _inspectorService = inspectorService;
            _departmentService = departmentService;
            _inspectorSessionService = inspectorSessionService;
        }

        [HttpGet("{id}")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetDepartmentById(Guid id)
        {
            var inspector = (Inspector)HttpContext.Items["User"];
            if (inspector == null)
            {
                if (!HttpContext.Request.Headers.TryGetValue("ApiKey", out var apiKey))
                    if (String.IsNullOrEmpty(apiKey) && apiKey.First()?.Split('@').First() == _key)
                        return Unauthorized();
                inspector = await _inspectorService.GetInspectorByTelegramUserIdAsync(apiKey.First().Split('@').Last()).ConfigureAwait(false);
                if (inspector == null)
                    return Unauthorized();
            } else
            {
                var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                if (inspectorSession == null) return Unauthorized();
                var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                if (session == null) return Unauthorized();
            }

            try
            {
                var department = await _departmentService.GetDepartmentByIdAsync(id).ConfigureAwait(false);
                return Ok(_mapper.Map<DepartmentViewModel>(department));
            }
            catch (DepartmentNotFoundException)
            {
                return NotFound();
            }
        }

        [HttpGet]
        [AuthorizeInspector]
        public async Task<IActionResult> GetDepartmentList([FromQuery] int page = 1,
                                                            [FromQuery] int perPage = 10)
        {
            if (page == null || page <= 0)
            {
                return BadRequest("Page must be equal to or greater than 1");
            }
            if (perPage == null || (perPage <= 0 || perPage > 100))
            {
                return BadRequest("perPage must be equal to or greater than 1 and less then 100");
            }

            var inspector = (Inspector)HttpContext.Items["User"];
            if (inspector == null)
            {
                if (!HttpContext.Request.Headers.TryGetValue("ApiKey", out var apiKey))
                    if (String.IsNullOrEmpty(apiKey) && apiKey.First()?.Split('@').First() == _key)
                        return Unauthorized();
                inspector = await _inspectorService.GetInspectorByTelegramUserIdAsync(apiKey.First().Split('@').Last()).ConfigureAwait(false);
                if (inspector == null)
                    return Unauthorized();
            }
            else
            {
                var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                if (inspectorSession == null) return Unauthorized();
                var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                if (session == null) return Unauthorized();
            }

            var departments = await _departmentService.GetDepartmentListAsync(page, perPage).ConfigureAwait(false);
            return Ok(_mapper.Map<IEnumerable<DepartmentViewModel>>(departments));
        }
    }
}
