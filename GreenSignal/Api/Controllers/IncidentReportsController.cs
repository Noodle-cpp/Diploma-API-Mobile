using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;
using Domain.Exceptions;
using Domain.Services;
using Domain.ViewModels;
using Infrastructure;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;

namespace Api.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class IncidentReportsController : ControllerBase
    {
        private readonly IIncidentReportService _incidentReportService;
        private readonly IIncidentReportAttributeService _incidentReportAttributeService;
        private readonly IMapper _mapper;
        private readonly IInspectorService _inspectorService;
        private readonly IInspectorSessionService _inspectorSessionService;
        private readonly string _key;

        public IncidentReportsController(IIncidentReportService incidentReportService,
                                            IMapper mapper,
                                            IIncidentReportAttributeService incidentReportAttributeService,
                                            IInspectorService inspectorService,
                                            IOptions<GreenSignalConfigurationOptions> options,
                                            IInspectorSessionService inspectorSessionService)
        {
            _incidentReportService = incidentReportService;
            _mapper = mapper;
            _incidentReportAttributeService = incidentReportAttributeService;
            _inspectorService = inspectorService;
            _key = options.Value.ApiKey;
            _inspectorSessionService = inspectorSessionService;
        }

        /// <summary>
        /// Создаёт обращение о нарушении
        /// </summary>
        /// <param name="incidentReportViewModel">Обращение</param>
        /// <returns>Обращение</returns>
        /// <response code="200">Обращение создан</response>
        /// <response code="400">Инспектор или локация не найдена</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="403">Инспектор не ведёт заявку</response>
        /// <response code="404">Обращение не найдено</response>
        /// <response code="422">Неверно заполненные поля</response>
        [ProducesResponseType(typeof(IncidentReportViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPost]
        [AuthorizeInspector]
        public async Task<IActionResult> CreateIncidentReport([FromBody] CreateIncidentReportViewModel incidentReportViewModel)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
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
                var newIncidentReport = _mapper.Map<IncidentReport>(incidentReportViewModel);
                var incidentReport = await _incidentReportService.CreateIncidentReportAsync(newIncidentReport, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentReportViewModel>(incidentReport));
            }
            catch (IncidentReportNotFoundException)
            {
                return NotFound();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (InspectorNotFoundException)
            {
                return BadRequest();
            }
            catch (LocationNotFoundException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Получает обращение по id
        /// </summary>
        /// <param name="id">Уникальны идентификатор</param>
        /// <returns>Обращение</returns>
        /// <response code="200">Обращение получен</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="404">Обращение не найдено</response>
        [ProducesResponseType(typeof(IncidentReportViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpGet("{id}")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetIncidentReportById(Guid id)
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
                var incidentReport = await _incidentReportService.GetIncidentReportByIdAsync(id, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentReportViewModel>(incidentReport));
            }
            catch (IncidentReportNotFoundException)
            {
                return NotFound();
            }
        }

        /// <summary>
        /// Получает список актов инспектора
        /// </summary>
        /// <param name="page">Номер страницы</param>
        /// <param name="perPage">Кол-во элементов на странице</param>
        /// <param name="incidentReportKind">Тип акта</param>
        /// <param name="incidentReportStatus">Статус акта</param>
        /// <returns>Список актов</returns>
        /// <response code="200">Список актов получен</response>
        /// <response code="400">Ошибка в аргументах пагинации</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        [ProducesResponseType(typeof(IEnumerable<IncidentReportViewModel>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [HttpGet]
        [AuthorizeInspector]
        public async Task<IActionResult> GetIncidentReportList([FromQuery] int page = 1,
                                                                [FromQuery] int perPage = 10,
                                                                [FromQuery] IncidentReportKind? incidentReportKind = null,
                                                                [FromQuery] IncidentReportStatus? incidentReportStatus = null)
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
                var incidentReport = await _incidentReportService.GetInicdentReportListAsync(inspector.Id, page, perPage, incidentReportKind, incidentReportStatus).ConfigureAwait(false);
                return Ok(_mapper.Map<IEnumerable<IncidentReportViewModel>>(incidentReport));
            }
            catch (Exception)
            {
                throw;
            }
        }

        /// <summary>
        /// Изменяет обращение о нарушении
        /// </summary>
        /// <param name="id">Уникальный идентификатор</param>
        /// <param name="incidentReportViewModel">Обновлённые поля обращения</param>
        /// <returns>Обращение</returns>
        /// <response code="200">Обращение изменён</response>
        /// <response code="400">Заявка или локация не найдена</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="403">Инспектор не создатель обращения</response>
        /// <response code="404">Обращение не найдено</response>
        /// <response code="422">Неверно заполненные поля</response>
        [ProducesResponseType(typeof(IncidentReportViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPut("{id}")]
        [AuthorizeInspector]
        public async Task<IActionResult> UpdateIncidentReport(Guid id, 
                                                                [FromBody] UpdateIncidentReportViewModel incidentReportViewModel)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
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
                var updatedIncidentReport = _mapper.Map<IncidentReport>(incidentReportViewModel);
                var incidentReport = await _incidentReportService.UpdateIncidentReportAsync(id, updatedIncidentReport, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentReportViewModel>(incidentReport));
            }
            catch (IncidentReportNotFoundException)
            {
                return NotFound();
            }
            catch (IncidentNotFoundException)
            {
                return BadRequest();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (LocationNotFoundException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Удаляет обращение
        /// </summary>
        /// <param name="id">Уникальный идентификатор</param>
        /// <returns></returns>
        /// <response code="200">Обращение удалён</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="403">Инспектор не создатель обращения</response>
        /// <response code="404">Обращение не найдено</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpDelete("{id}")]
        [AuthorizeInspector]
        public async Task<IActionResult> RemoveIncidentReport(Guid id)
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
                await _incidentReportService.RemoveIncidedntReportAsync(id, inspector.Id).ConfigureAwait(false);
                return Ok();
            }
            catch (IncidentReportNotFoundException)
            {
                return NotFound();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
        }

        /// <summary>
        /// Прикрепляет вложение к обращению
        /// </summary>
        /// <param name="id">Уникальный идентификатор обращения</param>
        /// <param name="createFileViewModel">Вложение</param>
        /// <returns>Обращение</returns>
        /// <response code="200">Вложение закреплено</response>
        /// <response code="400">Ошибка загрузки файла</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="403">Инспектор не создатель обращения</response>
        /// <response code="404">Обращение не найдено</response>
        /// <response code="422">Неверно заполненные поля</response>
        [ProducesResponseType(typeof(IncidentReportViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPut("{id}/attachments/attach")]
        [AuthorizeInspector]
        public async Task<IActionResult> AttachFileToIncidentReport(Guid id, 
                                                                    [FromForm] CreateIncidentReportAttachmentViewModel createFileViewModel)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
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
                var incidentReport = await _incidentReportService.AttachAttachmentToIncidentReportAsync(id, inspector.Id, createFileViewModel.File, createFileViewModel.Description, createFileViewModel.ManualDate).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentReportViewModel>(incidentReport));
            }
            catch (IncidentReportNotFoundException)
            {
                return NotFound();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (UploadAttachmentException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Прикрепляет вложение к обращению
        /// </summary>
        /// <param name="id">Уникальный идентификатор вложения</param>
        /// <returns>Обращение</returns>
        /// <response code="200">Вложение откреплено</response>
        /// <response code="400">Обращение не найдено</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="403">Инспектор не создатель обращения</response>
        /// <response code="404">Вложение не найдено</response>
        [ProducesResponseType(typeof(IncidentReportViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpPut("{incidentReportId}/attachments/{id}/detach")]
        [AuthorizeInspector]
        public async Task<IActionResult> DetachFileFromIncidentReport(Guid id)
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
                var incidentReport = await _incidentReportService.DetachAttachmentFromIncidentReportAsync(id, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentReportViewModel>(incidentReport));
            }
            catch (IncidentReportAttachmentNotFoundException)
            {
                return NotFound();
            }
            catch (IncidentReportNotFoundException)
            {
                return BadRequest();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
        }

        //TODO: Почему-то возвращается 204
        /// <summary>
        /// Изменяет информацию о вложении
        /// </summary>
        /// <param name="id">Уникальный идентификатор вложения</param>
        /// <param name="incidentReportAttachmentViewModel">Изменённые поля вложения</param>
        /// <returns>Обращение</returns>
        /// <response code="200">Вложение изменено</response>
        /// <response code="400">Обращение не найдено</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="403">Инспектор не создатель обращения</response>
        /// <response code="404">Вложение не найдено</response>
        [ProducesResponseType(typeof(IncidentReportViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpPut("{incidentReportId}/attachments/{id}")]
        [AuthorizeInspector]
        public async Task<IActionResult> UpdateIncidentReportAttachment(Guid id, 
                                                                        [FromBody] UpdateIncidentReportAttachmentViewModel incidentReportAttachmentViewModel)
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
                var incidentReport = await _incidentReportService.UpdateAttachmentFromIncidentReportAsync(id, inspector.Id, 
                                                                                                            incidentReportAttachmentViewModel.Description, 
                                                                                                            incidentReportAttachmentViewModel.ManualDate)
                                                                    .ConfigureAwait(false);
                return Ok(_mapper.Map<IncidentReportViewModel>(incidentReport));
            }
            catch (IncidentReportAttachmentNotFoundException)
            {
                return NotFound();
            }
            catch (IncidentReportNotFoundException)
            {
                return BadRequest();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
        }

        /// <summary>
        /// Изменяет атрибуты обращения
        /// </summary>
        /// <param name="id">Уникальный идентификатор обращения</param>
        /// <param name="attributesVM">Атрибуты</param>
        /// <returns>Обращение</returns>
        /// <response code="200">Атрибуты изменены</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="403">Инспектор не создатель обращения</response>
        /// <response code="404">Обращение не найдено</response>
        /// <response code="422">Обязательный атрибут не заполнен</response>
        [ProducesResponseType(typeof(IncidentReportViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPut("{id}/attributes/update")]
        [AuthorizeInspector]
        public async Task<IActionResult> IncidentReporAttributesUpdate(Guid id,
                                                                       [FromBody] IEnumerable<CreateIncidentReportAttributeViewModel> attributesVM)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
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
                var incident = await _incidentReportService.GetIncidentReportByIdAsync(id, inspector.Id).ConfigureAwait(false);
                await _incidentReportAttributeService.CreateAttributeList(_mapper.Map<IEnumerable<AttributeViewModel>>(attributesVM), incident).ConfigureAwait(false);
                return Ok();
            }
            catch (AttributeIsRequiredException ex)
            {
                return UnprocessableEntity(ex.Message);
            }
            catch (IncidentReportNotFoundException)
            {
                return NotFound();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
        }

        [ProducesResponseType(typeof(IncidentReportAttributeItemViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpGet("attributes")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetIncidentReporAttributes(IncidentReportKind incidentReportKind, int attributeVersion)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
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

                var attributes = _incidentReportAttributeService.GetAttributeList(incidentReportKind, attributeVersion);
                return Ok(attributes.Select(x => new IncidentReportAttributeItemViewModel()
                {
                    IsRequired = x.IsRequired,
                    Kind = x.Kind,
                    Name = x.Name,
                    Title = x.Title,
                    Type = x.Type.Name,
                    Version = x.Version
                }));
            }
            catch (AttributeIsRequiredException ex)
            {
                return UnprocessableEntity(ex.Message);
            }
            catch (IncidentReportNotFoundException)
            {
                return NotFound();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
        }

        /// <summary>
        /// Получает pdf файл обращения по id
        /// </summary>
        /// <param name="id">Уникальны идентификатор</param>
        /// <returns>Pdf-файл</returns>
        /// <response code="200">Pdf обращения получен</response>
        /// <response code="400">Не удаётся получить сохранённый файл</response>
        /// <response code="401">Пользователь не вошёл в систему</response>
        /// <response code="404">Обращение не найдено</response>
        [ProducesResponseType(typeof(IncidentReportViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpGet("{id}/pdf")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetPdfReport(Guid id)
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
                var incidentReport = await _incidentReportService.GetIncidentReportByIdAsync(id, inspector.Id).ConfigureAwait(false);
                var pdf = await _incidentReportService.GetIncidentReportPdfAsync(id, inspector.Id).ConfigureAwait(false);

                var result = new FileStreamResult(pdf, $"application/pdf")
                {
                    FileDownloadName = $"{incidentReport.SerialNumber}.pdf"
                };

                return result;
            }
            catch (SavedFileNotFoundException)
            {
                return BadRequest();
            }
            catch (IncidentReportNotFoundException)
            {
                return NotFound();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
        }

        /// <summary>
        /// Получает статистику по актам
        /// </summary>
        /// <returns>Статистику актов</returns>
        /// <response code="200">Статистика актов</response>
        [ProducesResponseType(typeof(ViewModels.Responses.IncidentReportStatisticViewModel), StatusCodes.Status200OK)]
        [AllowAnonymous]
        [HttpGet("Statistic")]
        public async Task<IActionResult> GetStatistic()
        {
            try
            {
                var sentIncidentReportCount = await _incidentReportService.GetStatistic().ConfigureAwait(false);
                return Ok(_mapper.Map<ViewModels.Responses.IncidentReportStatisticViewModel>(sentIncidentReportCount));
            }
            catch (Exception)
            {

                throw;
            }
        }

    }
}
