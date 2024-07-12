using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;
using Domain.Exceptions;
using Domain.Services;
using FileManager.Exceptions;
using Infrastructure;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using System.ComponentModel.DataAnnotations;

namespace Api.Controllers
{
    //[Route("[controller]")]
    [ApiController]
    public class IncidentsController : ControllerBase
    {
        private readonly IMapper _mapper;
        private readonly IIncidentService _incidentService;
        private readonly ICitizenService _citizenService;
        private readonly IInspectorService _inspectorService;
        private readonly IInspectorSessionService _inspectorSessionService;
        private readonly string _key;

        public IncidentsController(IMapper mapper,
                                    IIncidentService incidentService,
                                    ICitizenService citizenService,
                                    IInspectorService inspectorService,
                                    IOptions<GreenSignalConfigurationOptions> options,
                                    IInspectorSessionService inspectorSessionService)
        {
            _mapper = mapper;
            _incidentService = incidentService;
            _citizenService = citizenService;
            _inspectorService = inspectorService;
            _key = options.Value.ApiKey;
            _inspectorSessionService = inspectorSessionService;
        }

        /// <summary>
        /// Закрепляет вложение к заявке
        /// </summary>
        /// <param name="id">Идентификатор заявки</param>
        /// <param name="addAttachmentToIncident"></param>
        /// <returns>Заявку</returns>
        /// <response code="200">К заявке прикреплено вложение</response>
        /// <response code="400">Не получилось прикрепить ложение</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="403">Гражданин не является создателем заявки</response>
        /// <response code="404">Заявка не найдена</response>
        [ProducesResponseType(typeof(IncidentViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpPut("[controller]/{id}/file/attach")]
        [AuthorizeCitizen]
        public async Task<IActionResult> AttachFile(Guid id, [FromForm] AddAttachmentToIncidentViewModel addAttachmentToIncident)
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

            try
            {
                var incident = await _incidentService.AttachFileToIncidentAsync(id, citizen.Id, addAttachmentToIncident.File, addAttachmentToIncident.Description).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentViewModel>(incident));
            }
            catch (IncidentNotFoundException)
            {
                return NotFound();
            }
            catch (CitizenIsNotAnOwnerOfIncidentException)
            {
                return Forbid();
            }
            catch (UploadAttachmentException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Открепляет вложение из заявки
        /// </summary>
        /// <param name="id">Идентификатор вложения</param>
        /// <returns>Заявку</returns>
        /// <response code="200">Вложение откреплено от заявки</response>
        /// <response code="400">Не удалось удалить вложение</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="403">Гражданин не является создателем заявки</response>
        /// <response code="404">Заявка не найдена</response>
        [ProducesResponseType(typeof(IncidentViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpPut("[controller]/{incidentId}/file/{id}/detach")]
        [AuthorizeCitizen]
        public async Task<IActionResult> DetachFile(Guid id)
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

            try
            {
                var incident = await _incidentService.DetachFileFromIncidentAsync(id, citizen.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentViewModel>(incident));
            }
            catch (IncidentAttachmentNotFoundException)
            {
                return NotFound();
            }
            catch (IncidentNotFoundException)
            {
                return NotFound();
            }
            catch (SavedFileNotFoundException)
            {
                return NotFound();
            }
            catch (CitizenIsNotAnOwnerOfIncidentException)
            {
                return Forbid();
            }
            catch (RemoveFileException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Подтверждает создание заявки
        /// </summary>
        /// <param name="id">Идентификатор заявки</param>
        /// <returns>Заявку</returns>
        /// <response code="200">Заявка подтверждена</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="403">Гражданин не является создателем заявки</response>
        /// <response code="404">Заявка не найдена</response>
        [ProducesResponseType(typeof(IncidentViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpPut("[controller]/{id}/apply")]
        [AuthorizeCitizen]
        public async Task<IActionResult> ApplyIncident(Guid id)
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

            try
            {
                var incident = await _incidentService.ApplyIncidentAsync(id, citizen.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentViewModel>(incident));
            }
            catch (IncidentNotFoundException)
            {
                return NotFound();
            }
            catch (CitizenIsNotAnOwnerOfIncidentException)
            {
                return Forbid();
            }
        }

        /// <summary>
        /// Создаёт новую заявку
        /// </summary>
        /// <returns>Заявку</returns>
        /// <response code="200">Вложение откреплено от заявки</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="422">Неверно заполненные поля</response>
        [ProducesResponseType(typeof(IncidentViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPost("[controller]")]
        [AuthorizeCitizen]
        public async Task<IActionResult> CreateIncident([FromBody] CreateIncidentViewModel incientViewModel)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
            }

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

            try
            {
                var newIncident = _mapper.Map<Incident>(incientViewModel);
                newIncident.ReportedById = citizen.Id;

                var incident = await _incidentService.CreateIncidentAsync(newIncident).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentViewModel>(incident));
            }
            catch (Exception)
            {

                throw;
            }
        }

        /// <summary>
        /// Обновляет заявку
        /// </summary>
        /// <param name="id">Идентификатор заявки</param>
        /// <param name="updateIncidentViewModel"></param>
        /// <returns>Заявку</returns>
        /// <response code="200">Заявка изменена</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="403">Гражданин не является создателем заявки</response>
        /// <response code="404">Заявка не найдена</response>
        /// <response code="422">Неверно заполненные поля</response>
        [ProducesResponseType(typeof(IncidentViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPut("[controller]/{id}")]
        [AuthorizeCitizen]
        public async Task<IActionResult> UpdateIncident(Guid id, [FromBody] UpdateIncidentViewModel updateIncidentViewModel)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
            }

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

            try
            {
                var updatedIncient = _mapper.Map<Incident>(updateIncidentViewModel);
                var incident = await _incidentService.UpdateIncidentAsync(id, citizen.Id, updatedIncient).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentViewModel>(incident));
            }
            catch (IncidentNotFoundException)
            {
                return NotFound();
            }
            catch (CitizenIsNotAnOwnerOfIncidentException)
            {
                return Forbid();
            }
        }

        /// <summary>
        /// Получает ближайшие к инспектору заявки
        /// </summary>
        /// <returns>Список заявок</returns>
        /// <response code="200">Список заявок</response>
        /// <response code="400">Координаты отсутствуют или слишком старые</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        [ProducesResponseType(typeof(IEnumerable<IncidentViewModel>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [HttpGet("Inspectors/[controller]")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetIncidentsNerby([FromQuery] int page = 1,
                                                            [FromQuery] int perPage = 10,
                                                            [FromQuery] bool isNerby = false,
                                                            [FromQuery] IncidentKind? incidentKind = null)
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

            try
            {
                var incidents = isNerby ? await _incidentService.GetIncidentsNerbyAsync(inspector, page, perPage, incidentKind).ConfigureAwait(false)
                                        : await _incidentService.GetIncidentsAsync(page, perPage, incidentKind, IncidentStatus.Submitted);

                return Ok(_mapper.Map<IEnumerable<IncidentViewModel>>(incidents));
            }
            catch (CoordsAreEmptyException)
            {
                return BadRequest();
            }
            catch (CoordsAreTooOldException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Получает заявки для инспектора
        /// </summary>
        /// <returns>Список заявок</returns>
        /// <response code="200">Список заявок</response>
        /// <response code="400">Координаты отсутствуют или слишком старые</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        [ProducesResponseType(typeof(IEnumerable<IncidentViewModel>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [HttpGet("Inspectors/{id}/[controller]")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetIncidents(Guid id,
                                                        [FromQuery] int page = 1,
                                                        [FromQuery] int perPage = 10,
                                                        [FromQuery][EnumDataType(typeof(IncidentKind))] IncidentKind? incidentKind = null,
                                                        [FromQuery][EnumDataType(typeof(IncidentStatus))] IncidentStatus? incidentStatus = null)
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

            try
            {
                var incidents = await _incidentService.GetIncidentsAsync(page, perPage, incidentKind, incidentStatus, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<IEnumerable<IncidentViewModel>>(incidents));
            }
            catch (CoordsAreEmptyException)
            {
                return BadRequest();
            }
            catch (CoordsAreTooOldException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Получает заявки отправленные гражданином
        /// </summary>
        /// <returns>Список заявок</returns>
        /// <response code="200">Список заявок</response>
        /// <response code="400">Координаты отсутствуют или слишком старые</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        [ProducesResponseType(typeof(IEnumerable<IncidentViewModel>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [HttpGet("Citizens/[controller]/my")]
        [AuthorizeCitizen]
        public async Task<IActionResult> GetIncidents([FromQuery] int page = 1,
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

            try
            {
                var incidents = await _incidentService.GetIncidentsForCitizenAsync(citizen, page, perPage).ConfigureAwait(false);
                return Ok(_mapper.Map<IEnumerable<IncidentViewModel>>(incidents));
            }
            catch (Exception)
            {
                throw;
            }
        }

        /// <summary>
        /// Получает заявку для инспектора
        /// </summary>
        /// <param name="id">Идентификатор заявки</param>
        /// <returns>Заявку</returns>
        /// <response code="200">Заявка</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="404">Заявка не найдена</response>
        [ProducesResponseType(typeof(IncidentViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpGet("[controller]/{id}")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetIncidentById(Guid id) 
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
            }
            else
            {
                var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                if (inspectorSession == null) return Unauthorized();
                var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                if (session == null) return Unauthorized();
            }

            try
            {
                var incident = await _incidentService.GetForInspectorByIdAsync(id, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentViewModel>(incident));
            }
            catch (IncidentNotFoundException)
            {
                return NotFound();
            }
        }

        /// <summary>
        /// Прикрепляет заявку к инспектору
        /// </summary>
        /// <param name="id">Идентификатор заявки</param>
        /// <returns>Заявку</returns>
        /// <response code="200">Заявка</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="404">Заявка не найдена</response>
        [ProducesResponseType(typeof(IncidentViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpPut("[controller]/{id}/attach")]
        [AuthorizeInspector]
        public async Task<IActionResult> AttachIncidentToInspector(Guid id)
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
            }
            else
            {
                var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                if (inspectorSession == null) return Unauthorized();
                var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                if (session == null) return Unauthorized();
            }

            try
            {
                var incident = await _incidentService.AttachIncidentToInspectorAsync(id, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentViewModel>(incident));
            }
            catch (IncidentNotFoundException)
            {
                return NotFound();
            }
        }

        /// <summary>
        /// Получает статистику обращений
        /// </summary>
        /// <returns>Статистику обращений</returns>
        /// <response code="200">Статистика сообщений</response>
        [ProducesResponseType(typeof(IncidentStatisticViewModel), StatusCodes.Status200OK)]
        [HttpGet("[controller]/Statistic")]
        [AllowAnonymous]
        public async Task<IActionResult> GetStatistic()
        {
            try
            {
                var statistic = await _incidentService.GetStatisticAsync().ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentStatisticViewModel>(statistic));
            }
            catch (Exception)
            {

                throw;
            }
        }

        /// <summary>
        /// Отправляет жалобу
        /// </summary>
        /// <returns>Статистику обращений</returns>
        /// <response code="200">Статистика сообщений</response>
        [ProducesResponseType(typeof(IncidentStatisticViewModel), StatusCodes.Status200OK)]
        [HttpPut("[controller]/{id}/Report")]
        [AuthorizeInspector]
        public async Task<IActionResult> ReportIncident(Guid id, [FromQuery] ReportType reportType)
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
            }
            else
            {
                var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                if (inspectorSession == null) return Unauthorized();
                var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                if (session == null) return Unauthorized();
            }

            try
            {
                await _incidentService.ReportIncident(id, inspector.Id, reportType).ConfigureAwait(false);
                return Ok();
            }
            catch (IncidentNotFoundException)
            {
                return NotFound();
            }
            catch (InspectorAlreadyReportIncidentException)
            {
                return UnprocessableEntity();
            }
        }
    }
}
