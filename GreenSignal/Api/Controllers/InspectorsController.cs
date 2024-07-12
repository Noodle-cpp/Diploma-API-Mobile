using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;
using Domain.Exceptions;
using Domain.Services;
using FileManager.Exceptions;
using FirebaseAdmin.Messaging;
using Infrastructure;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using PdfViews.ViewModels;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace Api.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class InspectorsController : ControllerBase
    {
        private readonly IInspectorService _inspectorService;
        private readonly IAuthenticationService _authenticationService;
        private readonly IInspectorSessionService _inspectorSessionService;
        private readonly IMapper _mapper;
        private readonly IInspectorScoreService _inspectorScoreService;
        private readonly IReceiveMessageService _receiveMessageService;

        private readonly string _key;

        public InspectorsController(IInspectorService inspectorService,
                                    IAuthenticationService authenticationService,
                                    IInspectorSessionService inspectorSessionService,
                                    IMapper mapper,
                                    IInspectorScoreService inspectorScoreService,
                                    IReceiveMessageService receiveMessageService,
                                    IOptions<GreenSignalConfigurationOptions> options)
        {
            _inspectorService = inspectorService;
            _authenticationService = authenticationService;
            _inspectorSessionService = inspectorSessionService;
            _mapper = mapper;
            _inspectorScoreService = inspectorScoreService;
            _key = options.Value.ApiKey;
            _receiveMessageService = receiveMessageService;
        }

        /// <summary>
        /// Авторизирует инспектора
        /// </summary>
        /// <param name="authorizeViewModel"></param>
        /// <returns>Токен</returns>
        /// <response code="200">Инспектор получил токен</response>
        /// <response code="400">Инспектор или код не найдены</response>
        /// <response code="401">Код недоступен или невозможно выдать токен</response>
        /// <response code="422">Невалидные поля</response>
        [ProducesResponseType(typeof(TokenViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPost("Authorize")]
        public async Task<IActionResult> Authorize([FromBody] AuthorizeInspectorViewModel authorizeViewModel)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
            }

            try
            {
                var isCodeAvailable = await _authenticationService.ValidateCode(authorizeViewModel.Phone, authorizeViewModel.Code).ConfigureAwait(false);
                if (!isCodeAvailable)
                    return Unauthorized();

                var inspector = await _inspectorService.GetByPhoneAsync(authorizeViewModel.Phone).ConfigureAwait(false);

                if (inspector == null) return BadRequest();

                var inspectorSession = await _inspectorSessionService.CreateSessionAsync(inspector.Id, authorizeViewModel.FirebaseToken,
                                                                                            authorizeViewModel.DeviceName, HttpContext.Connection.RemoteIpAddress)
                                                                                            .ConfigureAwait(false);

                var tokenViewModel = await _authenticationService.AuthenticateInspector(authorizeViewModel.Phone, inspectorSession.Id).ConfigureAwait(false);

                if (tokenViewModel == null || tokenViewModel.InspectorId == null)
                    return BadRequest();

                await _authenticationService.RemoveCodeByPhone(authorizeViewModel.Phone).ConfigureAwait(false);
                return Ok(new TokenViewModel() { Token = tokenViewModel.Token });
            }
            catch (UserNotFoundException)
            {
                return BadRequest();
            }
            catch (CodeNotFoundException)
            {
                return BadRequest();
            }
        }

        [HttpPost("Logout")]
        [AuthorizeInspector]
        public async Task<IActionResult> Logout()
        {
            try
            {
                var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                if (inspectorSession == null) return Unauthorized();
                var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                if (session == null) return Unauthorized();

                await _inspectorSessionService.LogoutInspector(inspectorSession).ConfigureAwait(false);

                return Ok();
            }
            catch (InspectorSessionNotFound)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Получает код для авторизации инспектора
        /// </summary>
        /// <param name="authorizeViewModel"></param>
        /// <returns></returns>
        /// <response code="200">Код отправлен инспектору</response>
        /// <response code="400">Инспектор не найден</response>
        /// <response code="422">Невалидные поля</response>
        /// <response code="429">Слишком много запросов</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [ProducesResponseType(StatusCodes.Status429TooManyRequests)]
        [HttpPost("GetCode")]
        public async Task<IActionResult> GetCode([FromBody] GetCodeByPhoneViewModel authorizeViewModel)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
            }

            try
            {
                var inspector = await _inspectorService.GetByPhoneAsync(authorizeViewModel.Phone).ConfigureAwait(false);
                await _authenticationService.GetCode(authorizeViewModel.Phone).ConfigureAwait(false);
                return Ok();
            }
            catch (InspectorNotFoundException)
            {
                return BadRequest();
            }
            catch (CodeIsTooOldException)
            {
                return StatusCode(429);
            }
        }

        /// <summary>
        /// Получает код для регистрации инспектора
        /// </summary>
        /// <param name="authorizeViewModel"></param>
        /// <returns></returns>
        /// <response code="200">Код отправлен инспектору</response>
        /// <response code="400">Инспектор не найден</response>
        /// <response code="422">Невалидные поля</response>
        /// <response code="429">Слишком много запросов</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [ProducesResponseType(StatusCodes.Status429TooManyRequests)]
        [HttpPost("Registration/GetCode")]
        public async Task<IActionResult> GetCodeForRegistration([FromBody] GetCodeByPhoneViewModel authorizeViewModel)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
            }

            try
            {
                await _authenticationService.GetCode(authorizeViewModel.Phone).ConfigureAwait(false);
                return Ok();
            }
            catch (CodeIsTooOldException)
            {
                return StatusCode(429);
            }
        }

        /// <summary>
        /// Изменяет иниформации об инспекторе
        /// </summary>
        /// <param name="id">Уникальный идентификатор инспектора</param>
        /// <param name="inspectorViewModel">Объект инспектора с изменёнными полями</param>
        /// <returns>Объект изменённого инспектора</returns>
        /// <response code="200">Инспектор изменён</response>
        /// <response code="401">Инспектор не вошёл в систему</response>
        /// <response code="404">Инспектор не найден</response>
        /// <response code="422">Невалидные поля</response>
        [ProducesResponseType(typeof(ViewModels.Responses.InspectorViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPut("{id}")]
        [AuthorizeInspector]
        public async Task<IActionResult> UpdateInspector(Guid id, [FromBody] UpdateInspectorViewModel inspectorViewModel)
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
                var updatedInspector = await _inspectorService.UpdateInspectorAsync(id, _mapper.Map<Inspector>(inspectorViewModel)).ConfigureAwait(false);

                return Ok(_mapper.Map<ViewModels.Responses.InspectorViewModel>(updatedInspector));
            }
            catch (InspectorNotFoundException)
            {
                return NotFound();
            }
        }

        /// <summary>
        /// Изменяет фотографию инспектора
        /// </summary>
        /// <param name="id">Уникальный идентификатор инспектора</param>
        /// <param name="updateInspectorPhotoViewModel">Объект обновлённого фото</param>
        /// <returns>Объект изменённого инспектора</returns>
        /// <response code="200">Инспектор изменён</response>
        /// <response code="401">Инспектор не вошёл в систему</response>
        /// <response code="404">Инспектор не найден</response>
        /// <response code="422">Невалидные поля</response>
        [ProducesResponseType(typeof(ViewModels.Responses.InspectorViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPut("{id}/Photo")]
        [AuthorizeInspector]
        public async Task<IActionResult> UpdateInspectorPhoto(Guid id, [FromForm] UpdateInspectorPhotoViewModel updateInspectorPhotoViewModel)
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
                var updatedInspector = await _inspectorService.UpdateInspectorPhotoAsync(id, updateInspectorPhotoViewModel.InspectorPhoto).ConfigureAwait(false);

                return Ok(_mapper.Map<ViewModels.Responses.InspectorViewModel>(updatedInspector));
            }
            catch (InspectorNotFoundException)
            {
                return NotFound();
            }
        }

        /// <summary>
        /// Изменяет сертификат инспектора
        /// </summary>
        /// <param name="id">Уникальный идентификатор инспектора</param>
        /// <param name="updateInspectorCertificateViewModel">Объект обновлённого сертификата</param>
        /// <returns>Объект изменённого инспектора</returns>
        /// <response code="200">Инспектор изменён</response>
        /// <response code="401">Инспектор не вошёл в систему</response>
        /// <response code="404">Инспектор не найден</response>
        /// <response code="422">Невалидные поля</response>
        [ProducesResponseType(typeof(ViewModels.Responses.InspectorViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPut("{id}/Certificate")]
        [AuthorizeInspector]
        public async Task<IActionResult> UpdateInspectorCertificate(Guid id, [FromForm] UpdateInspectorCertificateViewModel updateInspectorCertificateViewModel)
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
                var updatedInspector = await _inspectorService.UpdateInspectorCertificateAsync(id, updateInspectorCertificateViewModel.CertificatePhoto).ConfigureAwait(false);

                return Ok(_mapper.Map<ViewModels.Responses.InspectorViewModel>(updatedInspector));
            }
            catch (InspectorNotFoundException)
            {
                return NotFound();
            }
        }

        /// <summary>
        /// Изменяет подпись инспектора
        /// </summary>
        /// <param name="id">Уникальный идентификатор инспектора</param>
        /// <param name="updateInspectorSignatureViewModel">Объект обновлённой подписи</param>
        /// <returns>Объект изменённого инспектора</returns>
        /// <response code="200">Инспектор изменён</response>
        /// <response code="401">Инспектор не вошёл в систему</response>
        /// <response code="404">Инспектор не найден</response>
        /// <response code="422">Невалидные поля</response>
        [ProducesResponseType(typeof(ViewModels.Responses.InspectorViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPut("{id}/Signature")]
        [AuthorizeInspector]
        public async Task<IActionResult> UpdateInspectorSignature(Guid id, [FromForm] UpdateInspectorSignatureViewModel updateInspectorSignatureViewModel)
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
                var updatedInspector = await _inspectorService.UpdateSignatureAsync(id, updateInspectorSignatureViewModel.SignaturePhoto).ConfigureAwait(false);

                return Ok(_mapper.Map<ViewModels.Responses.InspectorViewModel>(updatedInspector));
            }
            catch (InspectorNotFoundException)
            {
                return NotFound();
            }
        }

        /// <summary>
        /// Создаёт нового инспектора
        /// </summary>
        /// <param name="newInspectorViewModel"></param>
        /// <returns>Нового инспектора</returns>
        /// <response code="200">Инспектор создан, токен получен</response>
        /// <response code="400">Инспектор уже существует или файл невовзможно прикрепить</response>
        /// <response code="422">Невалидные поля</response>
        [ProducesResponseType(typeof(TokenViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPost]
        public async Task<IActionResult> CreateInspector([FromForm] CreateInspectorViewModel newInspectorViewModel)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
            }

            try
            {
                var isCodeAvailable = await _authenticationService.ValidateCode(newInspectorViewModel.Phone, newInspectorViewModel.Code).ConfigureAwait(false);
                if (!isCodeAvailable)
                    return Unauthorized();

                var newInspector = _mapper.Map<Inspector>(newInspectorViewModel);
                var inspector = await _inspectorService.CreateInspectorAsync(newInspector, newInspectorViewModel.InspectorPhoto, newInspectorViewModel.CertificatePhoto)
                                                        .ConfigureAwait(false);

                var inspectorSession = await _inspectorSessionService.CreateSessionAsync(inspector.Id, newInspectorViewModel.FirebaseToken,
                                                                                            newInspectorViewModel.DeviceName, HttpContext.Connection.RemoteIpAddress)
                                                                                            .ConfigureAwait(false);

                var tokenViewModel = await _authenticationService.AuthenticateInspector(newInspectorViewModel.Phone, inspectorSession.Id).ConfigureAwait(false);

                if (tokenViewModel == null || tokenViewModel.InspectorId == null)
                    return BadRequest();

                await _authenticationService.RemoveCodeByPhone(newInspectorViewModel.Phone).ConfigureAwait(false);

                return Ok(new TokenViewModel() { Token = tokenViewModel.Token });
            }
            catch (InspectorAlreadyExistsException)
            {
                return BadRequest();
            }
            catch (UploadFileException)
            {
                return BadRequest();
            }
            catch (CodeNotFoundException)
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Получает информацию об инспекторе
        /// </summary>
        /// <returns>Информацию об инспекторе</returns>
        /// <response code="200">Информация получена</response>
        /// <response code="401">Инспектор не вошёл в систему</response>
        [ProducesResponseType(typeof(ViewModels.Responses.InspectorViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [HttpGet("Profile")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetProfile()
        {
            var inspector = (Inspector)HttpContext.Items["User"];
            if (inspector == null) return Unauthorized();
            else
            {
                var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                if (inspectorSession == null) return Unauthorized();
                var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                if (session == null) return Unauthorized();
            }

            return Ok(_mapper.Map<ViewModels.Responses.InspectorViewModel>(inspector));
        }

        /// <summary>
        /// Обновляет местонахождение пользователя
        /// </summary>
        /// <returns>Информацию об инспекторе</returns>
        /// <response code="200">Координаты обновлены</response>
        /// <response code="401">Инспектор не вошёл в систему</response>
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [HttpPut("Location")]
        [AuthorizeInspector]
        public async Task<IActionResult> UpdateLocation(UpdateInspectorLocation updateInspectorLocation)
        {
            var inspector = (Inspector)HttpContext.Items["User"];
            if (inspector == null) return Unauthorized();
            else
            {
                var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                if (inspectorSession == null) return Unauthorized();
                var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                if (session == null) return Unauthorized();
            }

            try
            {
                await _inspectorService.UpdateInspectorLocationAsync(updateInspectorLocation.Lat, updateInspectorLocation.Lng, inspector).ConfigureAwait(false);
                return Ok();
            }
            catch (Exception)
            {
                throw;
            }
        }

        /// <summary>
        /// Получает историю баллов инспектора
        /// </summary>
        /// <param name="inspectorId"></param>
        /// <param name="page"></param>
        /// <param name="perPage"></param>
        /// <param name="startDate"></param>
        /// <param name="endDate"></param>
        /// <returns>История начисленных баллов</returns>
        /// <response code="200">История получена</response>
        /// <response code="401">Инспектор не вошел в систему</response>
        /// <response code="403">У инспектора нет доступа к истории</response>
        [ProducesResponseType(typeof(IEnumerable<InspectorScoreViewModel>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [HttpGet("{inspectorId}/Scores")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetScoreHistoryOfInspector(Guid inspectorId, 
                                                                    [FromQuery] int? page, [FromQuery] int? perPage, 
                                                                    [FromQuery] DateTime? startDate, [FromQuery] DateTime? endDate)
        {
            try
            {
                var inspector = (Inspector)HttpContext.Items["User"];
                if (inspector == null) return Unauthorized();
                else
                {
                    var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                    if (inspectorSession == null) return Unauthorized();
                    var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                    if (session == null) return Unauthorized();
                }

                var inspectorScore = await _inspectorScoreService.GetInspectorScoresAsync(inspector.Id, page, perPage, startDate, endDate).ConfigureAwait(false);

                return Ok(_mapper.Map<IEnumerable<InspectorScoreViewModel>>(inspectorScore));
            }
            catch (Exception)
            {

                throw;
            }
        }

        /// <summary>
        /// Получает рейтинг инспекторов
        /// </summary>
        /// <param name="startDate"></param>
        /// <param name="endDate"></param>
        /// <returns>История начисленных баллов</returns>
        /// <response code="200">Рейтинг получен</response>
        /// <response code="401">Инспектор не вошел в систему</response>
        [ProducesResponseType(typeof(IEnumerable<InspectorRatingScore>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [HttpGet("Rating")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetInspectorsRating([FromQuery] DateTime? startDate, [FromQuery] DateTime? endDate)
        {
            try
            {
                var inspector = (Inspector)HttpContext.Items["User"];
                if (inspector == null) return Unauthorized();
                else
                {
                    var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                    if (inspectorSession == null) return Unauthorized();
                    var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                    if (session == null) return Unauthorized();
                }

                var inspectorsRating = await _inspectorScoreService.GetInspectorsRatingAsync(inspector.Id, startDate, endDate).ConfigureAwait(false);

                return Ok(_mapper.Map<IEnumerable<InspectorRatingScore>>(inspectorsRating));
            }
            catch (Exception)
            {

                throw;
            }
        }

        /// <summary>
        /// Получает рейтинг инспекторов
        /// </summary>
        /// <param name="startDate"></param>
        /// <param name="endDate"></param>
        /// <returns>История начисленных баллов</returns>
        /// <response code="200">Рейтинг получен</response>
        /// <response code="401">Инспектор не вошел в систему</response>
        [ProducesResponseType(typeof(IEnumerable<InspectorRatingScore>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [HttpGet("{inspectorId}/Rating")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetInspectorRating([FromQuery] DateTime? startDate, [FromQuery] DateTime? endDate, Guid inspectorId)
        {
            try
            {
                var inspector = (Inspector)HttpContext.Items["User"];
                if (inspector == null) return Unauthorized();
                else
                {
                    var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                    if (inspectorSession == null) return Unauthorized();
                    var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                    if (session == null) return Unauthorized();
                }

                var inspectorRating = await _inspectorScoreService.GetInspectorRatingAsync(inspector.Id, startDate, endDate).ConfigureAwait(false);
                
                return Ok(_mapper.Map<InspectorRatingScore>(inspectorRating));
            }
            catch (InspectorNotFoundException)
            {
                return NotFound();
            }
        }

        /// <summary>
        /// Получает сообщения инспектора
        /// </summary>
        /// <param name="id"></param>
        /// <param name="filter">параметр фильтрации по имени отправителя, теме или содержанию</param>
        /// <returns>Список сообщений</returns>
        /// <response code="200">Сообщения получены</response>
        /// <response code="401">Инспектор не вошел в систему</response>
        /// <response code="403">У инспектора нет доступа к сообщениям</response>
        [ProducesResponseType(typeof(IEnumerable<ViewModels.Responses.ReceiveMessageViewModel>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [HttpGet("{id}/Messages")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetInspectorMessages(Guid id,
                                                                [FromQuery] string? filter = null)
        {
            try
            {
                var inspector = (Inspector)HttpContext.Items["User"];
                if (inspector == null) return Unauthorized();
                else
                {
                    var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                    if (inspectorSession == null) return Unauthorized();
                    var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                    if (session == null) return Unauthorized();
                }

                var messages = await _receiveMessageService.GetInspectorMessages(inspector.Id, filter).ConfigureAwait(false);

                return Ok(_mapper.Map<IEnumerable<ViewModels.Responses.ReceiveMessageViewModel>>(messages));
            }
            catch (Exception)
            {

                throw;
            }
        }

        /// <summary>
        /// Получает сообщение
        /// </summary>
        /// <param name="id"></param>
        /// <param name="messageId"></param>
        /// <returns>Сообщение</returns>
        /// <response code="200">Сообщение получено</response>
        /// <response code="401">Инспектор не вошел в систему</response>
        /// <response code="403">У инспектора нет доступа к сообщению</response>
        /// <response code="404">Сообщение не найдено</response>
        [ProducesResponseType(typeof(ViewModels.Responses.ReceiveMessageViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpGet("{id}/Messages/{messageId}")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetInspectorMessageById(Guid id, Guid messageId)
        {
            try
            {
                var inspector = (Inspector)HttpContext.Items["User"];
                if (inspector == null) return Unauthorized();
                else
                {
                    var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                    if (inspectorSession == null) return Unauthorized();
                    var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                    if (session == null) return Unauthorized();
                }

                var message = await _receiveMessageService.GetReceiveMessageByIdAsync(messageId, inspector.Id).ConfigureAwait(false);

                return Ok(_mapper.Map<ViewModels.Responses.ReceiveMessageViewModel>(message));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (ReceiveMessageNotFoundException)
            {
                return NotFound();
            }
        }

        /// <summary>
        /// Отмечает сообщение как прочитанное
        /// </summary>
        /// <param name="id"></param>
        /// <param name="messageId"></param>
        /// <returns></returns>
        /// <response code="200">Сообщение отмечено прочитанным</response>
        /// <response code="401">Инспектор не вошел в систему</response>
        /// <response code="403">У инспектора нет доступа к сообщению</response>
        /// <response code="404">Сообщение не найдено</response>
        [ProducesResponseType(typeof(ViewModels.Responses.ReceiveMessageViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpPut("{id}/Messages/{messageId}/MarkAsSeen")]
        [AuthorizeInspector]
        public async Task<IActionResult> MarkMessageAsSeen(Guid id, Guid messageId)
        {
            try
            {
                var inspector = (Inspector)HttpContext.Items["User"];
                if (inspector == null) return Unauthorized();
                else
                {
                    var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                    if (inspectorSession == null) return Unauthorized();
                    var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                    if (session == null) return Unauthorized();
                }

                var message = await _receiveMessageService.MarkMessageAsSeenAsync(messageId, inspector.Id).ConfigureAwait(false);

                return Ok(_mapper.Map<ViewModels.Responses.ReceiveMessageViewModel>(message));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (ReceiveMessageNotFoundException)
            {
                return NotFound();
            }
        }

        //TODO: Документация
        [HttpGet("{inspectorId}/Sessions")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetInspectorSessions(Guid inspectorId)
        {
            try
            {
                var inspector = (Inspector)HttpContext.Items["User"];
                if (inspector == null) return Unauthorized();
                else
                {
                    var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                    if (inspectorSession == null) return Unauthorized();
                    var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                    if (session == null) return Unauthorized();
                }

                var inspectorSessions = await _inspectorSessionService.GetInspectorsSessionsByInspectorIdAsync(inspector.Id).ConfigureAwait(false);

                return Ok(_mapper.Map<IEnumerable<InspectorSessionViewModel>>(inspectorSessions));
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
        }

        //TODO: Документация
        [HttpDelete("{inspectorId}/Sessions/{id}")]
        [AuthorizeInspector]
        public async Task<IActionResult> GetInspectorSessions(Guid inspectorId, Guid id)
        {
            try
            {
                var inspector = (Inspector)HttpContext.Items["User"];
                if (inspector == null) return Unauthorized();
                else
                {
                    var inspectorSession = (InspectorSession)HttpContext.Items["Session"];
                    if (inspectorSession == null) return Unauthorized();
                    var session = await _inspectorSessionService.GetInspectorSessionByIdAsync(inspectorSession.Id).ConfigureAwait(false);
                    if (session == null) return Unauthorized();
                }

                await _inspectorSessionService.RemoveInspectorSession(id, inspector.Id).ConfigureAwait(false);

                return Ok();
            }
            catch (InspectorNotAnOwnerException)
            {
                return Forbid();
            }
            catch (InspectorSessionNotFound)
            {
                return NotFound();
            }
        }


        /// <summary>
        /// Прикрепляет телеграм инспектора к системе
        /// </summary>
        /// <param name="attachInspectorTelegramToSystemViewModel"></param>
        /// <response code="200">Пользователь прикреплен к системе</response>
        /// <response code="404">Инспектор не найден</response>
        /// <returns></returns>
        [HttpPut("telegram/attach")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> AttachTelegramToInspector([FromBody] AttachInspectorTelegramToSystemViewModel attachInspectorTelegramToSystemViewModel)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
            }

            try
            {
                await _inspectorService.AttachInspectorTelegramAsync(attachInspectorTelegramToSystemViewModel.Phone, 
                                                                        attachInspectorTelegramToSystemViewModel.TelegramUserId, 
                                                                        attachInspectorTelegramToSystemViewModel.ChatId).ConfigureAwait(false);
                return Ok();
            }
            catch (InspectorNotFoundException)
            {
                return NotFound();
            }
        }


        /// <summary>
        /// Проверяет прикреплён лм к инспектору telegramId
        /// </summary>
        /// <response code="200">Пользователь прикреплен к системе</response>
        /// <response code="404">Инспектор не найден</response>
        /// <returns></returns>
        [HttpGet("telegram/check")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [AuthorizeInspector]
        public async Task<IActionResult> AttachTelegramToInspector()
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
                var result = await _inspectorService.CheckInspectorTelegramIdAsync(inspector.Id).ConfigureAwait(false);
                return Ok(result);
            }
            catch (InspectorNotFoundException)
            {
                return NotFound();
            }
        }
    }
}
