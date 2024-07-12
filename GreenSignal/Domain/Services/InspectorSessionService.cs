using Data.Models;
using Data.Repositories;
using Domain.Exceptions;
using Infrastructure;
using Microsoft.Extensions.Options;
using PushNotification;
using PushNotification.ViewModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Services
{
    public interface IInspectorSessionService
    {
        Task<InspectorSession> CreateSessionAsync(Guid inspectorId, string firebaseToken, string deviceName, IPAddress ip);
        Task<IEnumerable<InspectorSession>> GetInspectorsNerbyCoordsAsync(double lat, double lng);
        Task LogoutInspector(InspectorSession inspectorSession);
        Task<InspectorSession?> GetInspectorSessionByIdAsync(Guid inspectorSessionId);
        Task<IEnumerable<InspectorSession>> GetInspectorsSessionsByInspectorIdAsync(Guid inspectorId);
        Task RemoveInspectorSession(Guid sessionId, Guid inspectorId);
    }

    public class InspectorSessionService : IInspectorSessionService
    {
        private readonly IInspectorSessionRepository _inspectorSessionRepository;
        private readonly double _distanceKm;
        private readonly double _maxCoordsTimeAfterUpdate;
        INotificationGateway _notificationGateway;

        public InspectorSessionService(IInspectorSessionRepository inspectorSessionRepository,
                                        IOptions<GreenSignalConfigurationOptions> options,
                                        INotificationGateway notificationGateway)
        {
            _inspectorSessionRepository = inspectorSessionRepository;
            _distanceKm = options.Value.DistanceNotification.DistanceKm;
            _maxCoordsTimeAfterUpdate = options.Value.DistanceNotification.MaxTimeForLastCoordsUpdateHours;
            _notificationGateway = notificationGateway;
        }

        public async Task<InspectorSession> CreateSessionAsync(Guid inspectorId, string firebaseToken, string deviceName, IPAddress ip)
        {
            var newSession = new InspectorSession()
            {
                Id = Guid.NewGuid(),
                CretedAt = DateTime.UtcNow,
                DeviceName = deviceName,
                FirebaseToken = firebaseToken,
                InspectorId = inspectorId,
                Ip = ip.ToString()
            };

            var inspectorSessions = await GetInspectorsSessionsByInspectorIdAsync(inspectorId).ConfigureAwait(false);

            if(inspectorSessions.Any())
            {
                var notification = new NotificationViewModel()
                {
                    Title = $"Новый вход в систему",
                    MessageType = MessageType.NewSession,
                    Body = $"Произошёл в ход в систему с устройства {deviceName}\nС ip-адресом: {ip}",
                    ToId = newSession.Id
                };

                var result = await _notificationGateway.SendPushNotification(notification, inspectorSessions.Select(x => x.FirebaseToken)).ConfigureAwait(false);
                if (!result) Console.WriteLine("Уведомление отправлено с ошибкой");
            }

            await _inspectorSessionRepository.CreateSessionAsync(newSession).ConfigureAwait(false);

            return newSession;
        }

        public async Task RemoveInspectorSession(Guid sessionId, Guid inspectorId)
        {
            var inspectorSession = await _inspectorSessionRepository.GetInspectorSessionByIdAsync(sessionId).ConfigureAwait(false) ?? throw new InspectorSessionNotFound();
            if (inspectorSession.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            await _inspectorSessionRepository.RemoveInspectorSession(inspectorSession).ConfigureAwait(false);
        }

        public async Task<InspectorSession?> GetInspectorSessionByIdAsync(Guid inspectorSessionId)
        {
            return await _inspectorSessionRepository.GetInspectorSessionByIdAsync(inspectorSessionId).ConfigureAwait(false);
        }

        public async Task<IEnumerable<InspectorSession>> GetInspectorsNerbyCoordsAsync(double lat, double lng)
        {
            return await _inspectorSessionRepository.GetInspectorsNerbyCoordsAsync(lat, lng, _maxCoordsTimeAfterUpdate, _distanceKm).ConfigureAwait(false);
        }

        public async Task<IEnumerable<InspectorSession>> GetInspectorsSessionsByInspectorIdAsync(Guid inspectorId)
        {
            return await _inspectorSessionRepository.GetInspectorSessionsByInspectorIdAsync(inspectorId).ConfigureAwait(false);
        }

        public async Task LogoutInspector(InspectorSession inspectorSession)
        {
            await _inspectorSessionRepository.RemoveInspectorSession(inspectorSession).ConfigureAwait(false);
        }
    }
}
