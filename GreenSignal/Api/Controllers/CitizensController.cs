using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using Domain.Exceptions;
using Domain.Services;
using Microsoft.AspNetCore.Mvc;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace Api.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class CitizensController : ControllerBase
    {
        private readonly ICitizenService _citizenService;
        private readonly IAuthenticationService _authenticationService;

        public CitizensController(ICitizenService citizenService,
                                  IAuthenticationService authenticationService)
        {
            _citizenService = citizenService;
            _authenticationService = authenticationService;

        }

        /// <summary>
        /// Авторизирует гражданина
        /// </summary>
        /// <param name="authorizeViewModel"></param>
        /// <returns>Токен</returns>
        /// <returns></returns>
        /// <response code="200">Гражданиcн получил токен</response>
        /// <response code="400">Гражданин или код не найдены</response>
        /// <response code="401">Код недоступен или невозможно выдать токен</response>
        /// <response code="422">Неверно переданное тело</response>
        [ProducesResponseType(typeof(TokenViewModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        [HttpPost("Authorize")]
        public async Task<IActionResult> Authorize([FromBody] AuthorizeCitizenViewModel authorizeViewModel)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
            }

            try
            {
                await _citizenService.UpsertCitizen(authorizeViewModel.Phone, authorizeViewModel.FIO).ConfigureAwait(false);

                var isCodeAvailable = await _authenticationService.ValidateCode(authorizeViewModel.Phone, authorizeViewModel.Code).ConfigureAwait(false);
                if (!isCodeAvailable)
                    return Unauthorized();

                var tokenViewModel = await _authenticationService.AuthenticateCitizen(authorizeViewModel.Phone).ConfigureAwait(false);
                if (tokenViewModel == null)
                    return Unauthorized();

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

        /// <summary>
        /// Получает кода для гражданина
        /// </summary>
        /// <param name="authorizeViewModel"></param>
        /// <returns></returns>
        /// <returns></returns>
        /// <response code="200">Код отправлен гражданину</response>
        /// <response code="400">Гражданин не найден</response>
        /// <response code="422">Неверно переданное тело</response>
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
                await _authenticationService.GetCode(authorizeViewModel.Phone).ConfigureAwait(false);
                return Ok();
            }
            catch (CodeIsTooOldException)
            {
                return StatusCode(429);
            }
        }

        /// <summary>
        /// Прикрепляет телеграм гражданина к системе
        /// </summary>
        /// <param name="attachCitizenTelegramToSystem"></param>
        /// <response code="200">Пользователь прикреплен к системе</response>
        /// <response code="422">Неверно переданное тело</response>
        /// <returns></returns>
        [AuthorizeCitizen]
        [HttpPost("telegram/attach")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status422UnprocessableEntity)]
        public async Task<IActionResult> AttachTelegramToCitizen([FromBody] AttachCitizenTelegramToSystemViewModel attachCitizenTelegramToSystem)
        {
            if (!ModelState.IsValid)
            {
                return UnprocessableEntity(ModelState);
            }

            try
            {
                await _citizenService.UpsertCitizen(attachCitizenTelegramToSystem.Phone, attachCitizenTelegramToSystem.FIO, attachCitizenTelegramToSystem.TelegramUserId).ConfigureAwait(false);

                return Ok();
            }
            catch (Exception)
            {
                throw new Exception();
            }
        }
    }
}
