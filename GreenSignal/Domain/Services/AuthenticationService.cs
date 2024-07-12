using Data.Models;
using Data.Repositories;
using Domain.Exceptions;
using Domain.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;
using System.Security.Cryptography;
using Newtonsoft.Json.Linq;
using Microsoft.Extensions.Options;
using Infrastructure;
using System.Net;

namespace Domain.Services
{
    public interface IAuthenticationService
    {
        Task GetCode(string phone);
        Task<bool> ValidateCode(string phone, string accessCode);
        Task<AuthenticationToken> AuthenticateInspector(string phone, Guid sessionId);
        Task<AuthenticationToken> AuthenticateCitizen(string phone);
        ValidatedTokenViewModel ValidateToken(string token);
        Task RemoveCodeByPhone(string phone);
    }

    public class AuthenticationService : IAuthenticationService
    {
        private readonly IConfiguration _configuration;
        private readonly IInspectorRepository _inspectorRepository;
        private readonly ICodeRepository _codeRepository;
        private readonly ICitizenRepository _citizenRepository;
        private readonly IHttpClientFactory _httpClientFactory;

        public AuthenticationService(IConfiguration configuration,
                                        IInspectorRepository inspectorRepository,
                                        ICodeRepository codeRepository,
                                        ICitizenRepository citizenRepository,
                                        IHttpClientFactory httpClientFactory)
        {
            _configuration = configuration;
            _inspectorRepository = inspectorRepository;
            _codeRepository = codeRepository;
            _citizenRepository = citizenRepository;
            _httpClientFactory = httpClientFactory;
        }

        public async Task<AuthenticationToken> AuthenticateInspector(string phone, Guid sessionId)
        {
            var inspector = await _inspectorRepository.GetByPhoneAsync(phone).ConfigureAwait(false);

            return inspector == null ? throw new UserNotFoundException() : CreateAuthenticationToken(inspector, sessionId);
        }

        public async Task<AuthenticationToken> AuthenticateCitizen(string phone)
        {
            var citizen = await _citizenRepository.GetByPhoneAsync(phone).ConfigureAwait(false);

            return citizen == null ? throw new UserNotFoundException() : CreateAuthenticationToken(citizen);
        }

        public async Task GetCode(string phone)
        {
            var code = await _codeRepository.GetByPhoneAsync(phone).ConfigureAwait(false);

            if (code == null)
            {
                code = new()
                {
                    AccessCode = GenerateRandomNumber(),
                    CountOfRequests = 1,
                    DateLastRenewal = DateTime.UtcNow,
                    CreatedAt = DateTime.UtcNow,
                    Phone = phone,
                };
                await _codeRepository.CreateCodeAsync(code).ConfigureAwait(false);
                var inspector = await _inspectorRepository.GetByPhoneAsync(phone).ConfigureAwait(false);
                if(inspector != null && inspector.TelegramUserId != null && inspector.TelegramChatId != null)
                    await SendMessage(inspector.TelegramChatId, $"Ваш код {code.AccessCode}.\nНе сообщайте его никому\nЧтобы открыть меню нажмите /start", _configuration["TelegramBot:Token"]);
            }
            else if (IsAvailable(code))
            {
                code.AccessCode = GenerateRandomNumber();
                code.CountOfRequests++;
                code.DateLastRenewal = DateTime.UtcNow;
                await _codeRepository.UpdateCodeAsync(code).ConfigureAwait(false);

                var inspector = await _inspectorRepository.GetByPhoneAsync(phone).ConfigureAwait(false);
                if (inspector != null && inspector.TelegramUserId != null && inspector.TelegramChatId != null)
                    await SendMessage(inspector.TelegramChatId, $"Ваш код {code.AccessCode}.\nНе сообщайте его никому\nЧтобы открыть меню нажмите /start", _configuration["TelegramBot:Token"]);
            }
            else throw new CodeIsTooOldException();
        }

        public async Task<bool> ValidateCode(string phone, string accessCode)
        {
            var code = await _codeRepository.GetByPhoneAsync(phone).ConfigureAwait(false) ?? throw new CodeNotFoundException();
            if (code.AccessCode != accessCode) return false;

            return true;
        }

        public ValidatedTokenViewModel ValidateToken(string token)
        {
            if (token == null)
                return null;

            var tokenHandler = new JwtSecurityTokenHandler();
            var key = Encoding.ASCII.GetBytes(_configuration["JWT:Key"]);
            try
            {
                tokenHandler.ValidateToken(token, new TokenValidationParameters
                {
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = new SymmetricSecurityKey(key),
                    ValidateIssuer = false,
                    ValidateAudience = false,
                    // set clockskew to zero so tokens expire exactly at token expiration time (instead of 5 minutes later)
                    ClockSkew = TimeSpan.FromDays(1),
                }, out SecurityToken validatedToken);

                var jwtToken = (JwtSecurityToken)validatedToken;

                var userId = Guid.Parse(jwtToken.Claims.First(x => x.Type == "id").Value);
                
                var sessionIdClaim = jwtToken.Claims.FirstOrDefault(x => x.Type == "session_id");
                Guid? inspectorSessionId = sessionIdClaim != null ? Guid.Parse(sessionIdClaim.Value) : null;

                return new ValidatedTokenViewModel
                {
                    UserId = userId,
                    SessionId = inspectorSessionId,
                };
            }
            catch
            {
                // return null if validation fails
                return null;
            }
        }

        #region private methods

        private async Task SendMessage(string chatId, string message, string token)
        {
            string url = $"https://api.telegram.org/bot{token}/sendMessage?" +
                         $"chat_id={chatId}&" +
                         $"text={message}";
            var httpClient = _httpClientFactory.CreateClient();
            var response = await httpClient.GetAsync(new Uri(url)).ConfigureAwait(false);
        }

        private AuthenticationToken CreateAuthenticationToken(Inspector inspector, Guid sessionId)
        {
            var tokenHandler = new JwtSecurityTokenHandler();
            var tokenKey = Encoding.UTF8.GetBytes(_configuration["JWT:Key"]);

            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(new Claim[]
                {
                new("id", inspector.Id.ToString()),
                new("session_id", sessionId.ToString()),
                }),

                Expires = DateTime.UtcNow.AddMonths(1),
                SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(tokenKey),
                    SecurityAlgorithms.HmacSha256Signature)
            };

            var token = tokenHandler.CreateToken(tokenDescriptor);

            return new AuthenticationToken()
            {
                Token = tokenHandler.WriteToken(token),
                InspectorId = inspector.Id,
                SessionId = sessionId,
            };
        }

        private AuthenticationToken CreateAuthenticationToken(Citizen citizen)
        {
            var tokenHandler = new JwtSecurityTokenHandler();
            var tokenKey = Encoding.UTF8.GetBytes(_configuration["JWT:Key"]);

            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(new Claim[]
                {
                new("id", citizen.Id.ToString()),
                }),

                Expires = DateTime.UtcNow.AddMonths(1),
                SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(tokenKey),
                    SecurityAlgorithms.HmacSha256Signature)
            };

            var token = tokenHandler.CreateToken(tokenDescriptor);

            return new AuthenticationToken()
            {
                Token = tokenHandler.WriteToken(token),
                CitizenId = citizen.Id
            };
        }

        private static bool IsAvailable(Code code)
        {
            switch (code.CountOfRequests)
            {
                case <= 3:
                    return code.DateLastRenewal.AddMinutes(2) <= DateTime.UtcNow;
                case <= 5:
                    return code.DateLastRenewal.AddMinutes(30) <= DateTime.UtcNow;
                case >= 10:
                    return code.DateLastRenewal.AddDays(1) <= DateTime.UtcNow;
                case > 5:
                    return code.DateLastRenewal.AddHours(1) <= DateTime.UtcNow;
            }
        }

        private static string GenerateRandomNumber()
        {
            int size = 4;
            string a = "1234567890";
            StringBuilder result = new(size);
            using var rng = new RNGCryptoServiceProvider();
            while (result.Length < size)
            {
                var bytes = new byte[1];
                rng.GetBytes(bytes);
                if (bytes[0] >= (byte)(a.Length - 1)) continue;
                result.Append(a[bytes[0]]);
            }
            return result.ToString();
        }

        public async Task RemoveCodeByPhone(string phone)
        {
            var code = await _codeRepository.GetByPhoneAsync(phone).ConfigureAwait(false) ?? throw new CodeNotFoundException();
            await _codeRepository.RemoveCodeAsync(code).ConfigureAwait(false);
        }

        #endregion
    }

    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method)]
    public class AuthorizeInspectorAttribute : Attribute, IAuthorizationFilter
    {
        public void OnAuthorization(AuthorizationFilterContext context)
        {
            var config = new ConfigurationBuilder()
            .AddJsonFile("appsettings.json")
            .Build();

            string key = config["ApiKey"];

            // skip authorization if action is decorated with [AllowAnonymous] attribute
            var allowAnonymous = context.ActionDescriptor.EndpointMetadata.OfType<AllowAnonymousAttribute>().Any();
            if (allowAnonymous)
                return;

            // authorization
            var user = (Inspector)context.HttpContext.Items["User"];
            if (user != null)
                return;

            if (context.HttpContext.Request.Headers.TryGetValue("ApiKey", out var apiKey))
                if (!String.IsNullOrEmpty(apiKey) && apiKey.First()?.Split('@').First() == key)
                    return;

            context.Result = new JsonResult(new { message = "Unauthorized" }) { StatusCode = StatusCodes.Status401Unauthorized };
        }
    }

    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method)]
    public class AuthorizeCitizenAttribute : Attribute, IAuthorizationFilter
    {
        public void OnAuthorization(AuthorizationFilterContext context)
        {
            var config = new ConfigurationBuilder()
            .AddJsonFile("appsettings.json")
            .Build();

            string key = config["ApiKey"];

            // skip authorization if action is decorated with [AllowAnonymous] attribute
            var allowAnonymous = context.ActionDescriptor.EndpointMetadata.OfType<AllowAnonymousAttribute>().Any();
            if (allowAnonymous)
                return;

            // authorization
            var user = (Citizen)context.HttpContext.Items["User"];
            if (user != null)
                return;

            if (context.HttpContext.Request.Headers.TryGetValue("ApiKey", out var apiKey))
                if (!String.IsNullOrEmpty(apiKey) && apiKey.First()?.Split('@').First() == key)
                    return;

            context.Result = new JsonResult(new { message = "Unauthorized" }) { StatusCode = StatusCodes.Status401Unauthorized };
        }
    }
}
