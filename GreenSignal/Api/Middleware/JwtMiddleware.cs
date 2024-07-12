using Domain.Services;
using Microsoft.AspNetCore.Authentication;

namespace Api.Middleware
{
    public class JwtMiddleware
    {
        private readonly RequestDelegate _next;

        public JwtMiddleware(RequestDelegate next)
        {
            _next = next;
        }

        public async Task InvokeAsync(HttpContext context, 
                                        IInspectorService inspectorService,
                                        IInspectorSessionService inspectorSessionService,
                                        ICitizenService citizenService,
                                        Domain.Services.IAuthenticationService authenticationService)
        {
            var token = context.Request.Headers["Authorization"].FirstOrDefault()?.Split(" ").Last();

            var userId = authenticationService.ValidateToken(token)?.UserId;
            var sessionId = authenticationService.ValidateToken(token)?.SessionId;

            if (userId != null)
            {
                // attach user to context on successful jwt validation
                var inspector = await inspectorService.GetByIdAsync(userId.Value).ConfigureAwait(false);
                if (inspector != null)
                {
                    context.Items["User"] = inspector;

                    if(sessionId != null)
                    {
                        var inspectorSession = await inspectorSessionService.GetInspectorSessionByIdAsync(sessionId.Value).ConfigureAwait(false);
                        context.Items["Session"] = inspectorSession;
                    }
                }
                else
                {
                    var citizen = await citizenService.GetByIdAsync(userId.Value).ConfigureAwait(false);
                    context.Items["User"] = citizen;
                }
            }

            await _next(context);
        }
    }

    public static class JwtMiddlewareExtensions
    {
        public static IApplicationBuilder UseJwtMiddleware(
            this IApplicationBuilder builder)
        {
            return builder.UseMiddleware<JwtMiddleware>();
        }
    }
}
