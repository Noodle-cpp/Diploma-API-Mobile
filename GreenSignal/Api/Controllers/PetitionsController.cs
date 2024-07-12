using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;
using Domain.Exceptions;
using Domain.Services;
using Domain.ViewModels;
using Infrastructure;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;

namespace Api.Controllers
{
    [Route("[controller]")]
    [ApiController]
    //TODO: отправить pdf в ведомство
    //TODO: доделать документацию

    public class PetitionsController : ControllerBase
    {
        private readonly IPetitionService _petitionService;
        private readonly IReceiveMessageService _receiveMessageService;
        private readonly IPetitionAttributeService _petitionAttributeService;
        private readonly IMapper _mapper;
        private readonly string _key;
        private readonly IInspectorService _inspectorService;
        private readonly IInspectorSessionService _inspectorSessionService;

        public PetitionsController(IPetitionService petitionService,
                                    IMapper mapper,
                                    IPetitionAttributeService petitionAttributeService,
                                    IReceiveMessageService receiveMessageService,
                                    IOptions<GreenSignalConfigurationOptions> options,
                                    IInspectorService inspectorService,
                                    IInspectorSessionService inspectorSessionService)
        {
            _petitionService = petitionService;
            _mapper = mapper;
            _receiveMessageService = receiveMessageService;
            _petitionAttributeService = petitionAttributeService;
            _key = options.Value.ApiKey;
            _inspectorService = inspectorService;
            _inspectorSessionService = inspectorSessionService;
        }

        /// <summary>
        /// Получает список актов инспектора
        /// </summary>
        /// <param name="page">Номер страницы</param>
        /// <param name="perPage">Кол-во элементов на странице</param>
        /// <returns>Список актов</returns>
        /// <response code="200">Список актов получен</response>
        /// <response code="400">Неверно указана пагинация</response>
        /// <response code="401">Инспектор не вошёл в систему</response>
        [ProducesResponseType(typeof(IEnumerable<PetitionViewModel>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [HttpGet]
        [AuthorizeInspector]
        public async Task<IActionResult> GetPetitionList([FromQuery] int page = 1, [FromQuery] int perPage = 10)
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
                var petitions = await _petitionService.GetPetitionListAsync(inspector.Id, page, perPage).ConfigureAwait(false);
                return Ok(_mapper.Map<IEnumerable<PetitionViewModel>>(petitions));
            }
            catch (Exception)
            {

                throw;
            }
        }

        /// <summary>
        /// Получает акт
        /// </summary>
        /// <param name="id">Идентификатор акта</param>
        /// <returns>Акт</returns>
        /// <response code="200">Акт получен</response>
        /// <response code="401">Инспектор не вошёл в систему</response>
        /// <response code="403">Нет доступа к акту</response>
        /// <response code="404">Акт не найден</response>
        [ProducesResponseType(typeof(PetitionViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpGet("{id}")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetPetitionById(Guid id)
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
                var petition = await _petitionService.GetPetitionByIdAsync(id, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<PetitionViewModel>(petition));
            }
            catch (PetitionNotFoundException)
            {
                return NotFound();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
        }

        /// <summary>
        /// Создаёт новый акт
        /// </summary>
        /// <param name="petitionViewModel"></param>
        /// <returns>Созданный акт</returns>
        /// <response code="200">Акт создан</response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не вошел в систему</response>
        /// <response code="403">Нет доступа к обращению</response>
        /// <response code="422">Неверно заполненные поля</response>
        [ProducesResponseType(typeof(PetitionViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPost]
        [AuthorizeInspector]
        public async Task<IActionResult> CreatePetition([FromBody] CreatePetitionViewModel petitionViewModel)
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

            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
            }

            try
            {
                var newPetition = _mapper.Map<Petition>(petitionViewModel);
                var petition = await _petitionService.CreatePetitionAsync(newPetition, inspector.Id).ConfigureAwait(false);

                return Ok(_mapper.Map<PetitionViewModel>(petition));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (IncidentReportNotFoundException)
            {
                return BadRequest();
            }
            catch (DepartmentNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionMustHaveAParentException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Обновляет акт
        /// </summary>
        /// <param name="id">Идентификатор акта</param>
        /// <param name="petitionViewModel"></param>
        /// <returns>Обновлённый акт</returns>
        /// <responses code="200">Акт обновлён</responses>
        /// <responses code="400">Неверно заполненные поля</responses>
        /// <responses code="401">Инспектор не вошёл в систему</responses>
        /// <responses code="403">У инспектора нет доступа к акту</responses>
        /// <responses code="404">Акт не найден</responses>
        /// <responses code="422">Неверно заполненные поля</responses>
        [ProducesResponseType(typeof(PetitionViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPut("{id}")]
        [AuthorizeInspector]
        public async Task<IActionResult> UpdatePetition(Guid id, [FromBody] UpdatePetitionViewModel petitionViewModel)
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

            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
            }

            try
            {
                var updatePetition = _mapper.Map<Petition>(petitionViewModel);
                var petition = await _petitionService.UpdatePetitionAsync(id, updatePetition, inspector.Id).ConfigureAwait(false);

                return Ok(_mapper.Map<PetitionViewModel>(petition));
            }
            catch (PetitionMustHaveAParentException)
            {
                return BadRequest();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return NotFound();
            }
            catch (ParentPetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (IncidentReportNotFoundException)
            {
                return BadRequest();
            }
            catch (DepartmentNotFoundException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Удаляет акт
        /// </summary>
        /// <param name="id">Идентификатор акта</param>
        /// <returns></returns>
        /// <response code="200">Акту присвоен новый статус</response>
        /// <response code="401">Инспектор не вошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        /// <response code="404">Акт не найден</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpDelete("{id}")]
        [AuthorizeInspector]
        public async Task<IActionResult> RemovePetition(Guid id)
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
                await _petitionService.RemovePetitionAsync(id, inspector.Id).ConfigureAwait(false);
                return Ok();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return NotFound();
            }
        }

        /// <summary>
        /// Прикрепляет сообщение к акту
        /// </summary>
        /// <param name="id">Идентификатор акта</param>
        /// <param name="receiveMessageId">Идентификатор сообщения</param>
        /// <returns></returns>
        /// <response code="200"></response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [HttpPut("{id}/receiveMessages/{receiveMessageId}/attach")]
        [AuthorizeInspector]
        public async Task<IActionResult> AttachMessage(Guid id, Guid receiveMessageId)
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
                await _receiveMessageService.AttachMessageToPetitionAsync(receiveMessageId, id, inspector.Id).ConfigureAwait(false);
                return Ok();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (ReceiveMessageIsAlreadyAttachedException)
            {
                return BadRequest();
            }
            catch (ReceiveMessageNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionIsAlreadyCloseException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Открепляет сообщение от акта
        /// </summary>
        /// <param name="id">Идентификатор акта</param>
        /// <param name="receiveMessageId">Идентификатор сообщения</param>
        /// <returns></returns>
        /// <response code="200"></response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [HttpPut("{id}/receiveMessages/{receiveMessageId}/detach")]
        [AuthorizeInspector]
        public async Task<IActionResult> DetachMessage(Guid id, Guid receiveMessageId)
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
                await _receiveMessageService.DetachMessageFromPetitionAsync(receiveMessageId, id, inspector.Id).ConfigureAwait(false);
                return Ok();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (ReceiveMessageNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionIsAlreadyCloseException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Закрывает акт как успешно выполненный
        /// </summary>
        /// <param name="id">Идентификатор акта</param>
        /// <returns></returns>
        /// <response code="200">Закрытый акт</response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        /// <response code="404">Акт не найден</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpPut("{id}/close/successed")]
        [AuthorizeInspector]
        public async Task<IActionResult> ClosePetitionAsSuccessed(Guid id)
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
                var petition = await _petitionService.ClosePetitionAsync(id, inspector.Id, true).ConfigureAwait(false);
                return Ok(_mapper.Map<PetitionViewModel>(petition));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (ReceiveMessageNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionIsAlreadyCloseException)
            {
                return BadRequest();
            }
            catch (PetitionWasNotSentToADepartmentException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Отправляет акт в министерство
        /// </summary>
        /// <param name="id">Идентификатор акта</param>
        /// <returns></returns>
        /// <response code="200"></response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        /// <response code="404">Акт не найден</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpPut("{id}/sent")]
        [AuthorizeInspector]
        public async Task<IActionResult> SentPetition(Guid id)
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
                await _petitionService.SentPetitionAsync(id, inspector.Id).ConfigureAwait(false);
                return Ok();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Закрывает акт как выполненный не успешно 
        /// </summary>
        /// <param name="id">Идентификатор акта</param>
        /// <returns></returns>
        /// <response code="200">Закрытый акт</response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        /// <response code="404">Акт не найден</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpPut("{id}/close/failed")]
        [AuthorizeInspector]
        public async Task<IActionResult> ClosePetitionAsFailed(Guid id)
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
                var petition = await _petitionService.ClosePetitionAsync(id, inspector.Id, false).ConfigureAwait(false);
                return Ok(_mapper.Map<PetitionViewModel>(petition));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return NotFound();
            }
            catch (ReceiveMessageNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionIsAlreadyCloseException)
            {
                return BadRequest();
            }
            catch (PetitionWasNotSentToADepartmentException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Прикрепляет вложение к обращению
        /// </summary>
        /// <param name="attachmentToPetition">Вложение</param>
        /// <returns></returns>
        /// <response code="200"></response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [HttpPut("{id}/attachments/{attachmentId}/attach")]
        [AuthorizeInspector]
        public async Task<IActionResult> PetitionAttachmentAdd([FromForm] AddAttachmentToPetition attachmentToPetition)
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
                var petition = await _petitionService.PetitionAttachmentAdd(inspector.Id, attachmentToPetition.PetitionId, attachmentToPetition.File, attachmentToPetition.Description, attachmentToPetition.Lat,
                                                                            attachmentToPetition.Lng, attachmentToPetition.ManualDate, attachmentToPetition.CreatedAt).ConfigureAwait(false);
                return Ok(_mapper.Map<PetitionViewModel>(petition));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (ReceiveMessageNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionIsAlreadyCloseException)
            {
                return BadRequest();
            }
            catch (UploadAttachmentException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Открепляет вложение от обращения
        /// </summary>
        /// <param name="attachmentId">Идентификатор вложения</param>
        /// <returns></returns>
        /// <response code="200"></response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [HttpPut("{id}/attachments/{attachmentId}/detach")]
        [AuthorizeInspector]
        public async Task<IActionResult> PetitionAttachmentAdd(Guid attachmentId)
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
                var petition = await _petitionService.PetitionAttachmentRemove(attachmentId, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<PetitionViewModel>(petition));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (ReceiveMessageNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionIsAlreadyCloseException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Обновляет аттрибуты обращенмя
        /// </summary>
        /// <param name="id">Идентификатор обращения</param>
        /// <param name="attributesVM">Аттрибуты обращения</param>
        /// <returns></returns>
        /// <response code="200"></response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [HttpPut("{id}/attributes/update")]
        [AuthorizeInspector]
        public async Task<IActionResult> PetitionAttributesUpdate(Guid id, [FromBody] IEnumerable<CreatePetitionAttributeViewModel> attributesVM)
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
                var petition = await _petitionService.GetPetitionByIdAsync(id, inspector.Id).ConfigureAwait(false);
                await _petitionAttributeService.CreateAttributeList(_mapper.Map<IEnumerable<AttributeViewModel>>(attributesVM), petition).ConfigureAwait(false);
                return Ok();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionIsAlreadyCloseException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Получает аттрибуты для типа обращенмя
        /// </summary>
        /// <param name="petitionKind">Тип обращения</param>
        /// <param name="attributeVersion">Версия аттрибутов</param>
        /// <returns></returns>
        /// <response code="200"></response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpGet("attributes")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetPetitionAttributes(PetitionKind petitionKind, int attributeVersion)
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

                var attributes = _petitionAttributeService.GetAttributeList(petitionKind, attributeVersion);
                return Ok(attributes.Select(x => new PetitionAttributeItemViewModel()
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
        /// Прикрепляет акт к обращению
        /// </summary>
        /// <param name="id">Идентификатор обращения</param>
        /// <param name="incidentReportId">Идентификатор акта</param>
        /// <returns></returns>
        /// <response code="200"></response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [HttpPut("{id}/incidentReports/{incidentReportId}/attach")]
        [AuthorizeInspector]
        public async Task<IActionResult> AttachIncidentReportToPetition(Guid id, Guid incidentReportId)
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
                var petition = await _petitionService.AttachIncidentReportToPetitionAsync(id, incidentReportId, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<PetitionViewModel>(petition));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionIsAlreadyCloseException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Открепляет акт от обращения
        /// </summary>
        /// <param name="id">Идентификатор обращения</param>
        /// <returns></returns>
        /// <response code="200"></response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [HttpPut("{id}/incidentReports/{incidentReportId}/detach")]
        [AuthorizeInspector]
        public async Task<IActionResult> DetachIncidentReportFromPetition(Guid id)
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
                var petition = await _petitionService.DetachIncidentReportFromPetitionAsync(id, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<PetitionViewModel>(petition));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionAlreadyHasAParentException)
            {
                return BadRequest();
            }
            catch (PetitionIsAlreadyCloseException)
            {
                return BadRequest();
            }
            catch (IncidentReportNotFoundException)
            {
                return BadRequest();
            }
            catch (ParentIsAlreadyAttachedException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Закрепляет обращение к обращению
        /// </summary>
        /// <param name="id">Идентификатор обращения</param>
        /// <param name="parentId">Идентификатор родительского обращения</param>
        /// <returns></returns>
        /// <response code="200"></response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [HttpPut("{id}/petitions/{parentId}/attach")]
        [AuthorizeInspector]
        public async Task<IActionResult> AttachParentForPetition(Guid id, Guid parentId)
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
                var petition = await _petitionService.AttachPetitionToParent(id, parentId, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<PetitionViewModel>(petition));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionIsAlreadyCloseException)
            {
                return BadRequest();
            }
            catch (PetitionCantBeAParentOfHimselfException)
            {
                return BadRequest();
            }
        }

        [HttpPut("{id}/petitions/{parentId}/detach")]
        [AuthorizeInspector]
        public async Task<IActionResult> DetachParentFromPetition(Guid id)
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
                var petition = await _petitionService.DetachParentFromPetitionAsync(id, inspector.Id).ConfigureAwait(false);
                return Ok(_mapper.Map<PetitionViewModel>(petition));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (PetitionNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionIsAlreadyCloseException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Получает pdf-файл обращения
        /// </summary>
        /// <param name="id">Идентификатор обращения</param>
        /// <returns></returns>
        /// <response code="200"></response>
        /// <response code="400">Неверно заполненные поля</response>
        /// <response code="401">Инспектор не ввошёл в систему</response>
        /// <response code="403">У инспектора нет прав доступа</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [HttpGet("{id}/pdf")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetPdfFile(Guid id)
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
                var petition = await _petitionService.GetPetitionByIdAsync(id, inspector.Id).ConfigureAwait(false);
                var pdf = await _petitionService.GetPetitionPdfAsync(id, inspector.Id).ConfigureAwait(false);

                var result = new FileStreamResult(pdf, $"application/pdf")
                {
                    FileDownloadName = $"{petition.SerialNumber}.pdf"
                };

                return result;
            }
            catch (SavedFileNotFoundException)
            {
                return BadRequest();
            }
            catch (PetitionNotFoundException)
            {
                return NotFound();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
        }
    }
}
