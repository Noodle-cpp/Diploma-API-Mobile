using Data.Models;
using Data.Repositories;
using Domain.Exceptions;
using Domain.ViewModels;
using Infrastructure;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Options;
using PushNotification;
using PushNotification.ViewModels;
using System.Transactions;

namespace Domain.Services
{
    public interface IIncidentService
    {
        Task<Incident> GetForInspectorByIdAsync(Guid incidentId, Guid inspectorId);
        Task<Incident> CreateIncidentAsync(Incident incident);
        Task<Incident> ApplyIncidentAsync(Guid id, Guid citizenId);
        Task<Incident> AttachFileToIncidentAsync(Guid id, Guid citizenId, IFormFile file, string description);
        Task<Incident> DetachFileFromIncidentAsync(Guid id, Guid citizenId);
        Task<Incident> AttachIncidentToInspectorAsync(Guid id, Guid inspectorId);
        Task<IEnumerable<Incident>> GetIncidentsNerbyAsync(Inspector inspector, 
                                                            int page, int perPage,
                                                            IncidentKind? incidentKind);
        Task<IEnumerable<Incident>> GetIncidentsAsync(int page, int perPage,
                                                        IncidentKind? incidentKind,
                                                        IncidentStatus? incidentStatus,
                                                        Guid? inspectorId = null);
        Task<Incident> UpdateIncidentAsync(Guid id, Guid citizenId, Incident updatedIncident);
        Task<IEnumerable<Incident>> GetIncidentsForCitizenAsync(Citizen citizen, int page, int perPage);
        Task<IncidentStatisticViewModel> GetStatisticAsync();
        Task DecreaseInspectorsWithOverdueIncidentsAsync();
        Task ReportIncident(Guid incidentId, Guid inspectorId, ReportType reportType);
    }

    public class IncidentService : IIncidentService
    {
        private readonly IIncidentRepository _incidentRepository;
        private readonly IInspectorSessionService _inspectorSessionService;
        private readonly ISavedFileService _savedFileService;
        private readonly IIncidentAttachmentRepository _incidentAttachmentRepository;
        private readonly INotificationGateway _notificationGateway;
        private readonly IInspectorScoreService _inspectorScoreService;
        private readonly ICitizenRepository _citizenRepository;
        private readonly IIncidentComplaintRepository _incidentComplaintRepository;
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly double _maxDistance;
        private readonly double _maxTimeForCoordsUpdate;
        private readonly int _overdueDays;
        private readonly string _telegramToken;

        public IncidentService(IIncidentRepository incidentRepository,
                                 IInspectorSessionService inspectorSessionService,
                                 ISavedFileService savedFileService,
                                 IIncidentAttachmentRepository incidentAttachmentRepository,
                                 INotificationGateway notificationGateway,
                                 IInspectorScoreService inspectorScoreService,
                                 ICitizenRepository citizenRepository,
                                 IOptions<GreenSignalConfigurationOptions> options,
                                 IIncidentComplaintRepository incidentComplaintRepository,
                                 IHttpClientFactory httpClientFactory)
        {
            _incidentRepository = incidentRepository;
            _inspectorSessionService = inspectorSessionService;
            _savedFileService = savedFileService;
            _incidentAttachmentRepository = incidentAttachmentRepository;
            _notificationGateway = notificationGateway;
            _inspectorScoreService = inspectorScoreService;
            _maxDistance = options.Value.DistanceNotification.DistanceKm;
            _maxTimeForCoordsUpdate = options.Value.DistanceNotification.MaxTimeForLastCoordsUpdateHours;
            _telegramToken = options.Value.TelegramBot.Token;
            _overdueDays = options.Value.InspectorScore.OverdueDays;
            _citizenRepository = citizenRepository;
            _incidentComplaintRepository = incidentComplaintRepository;
            _httpClientFactory = httpClientFactory;
        }

        public async Task<Incident> ApplyIncidentAsync(Guid id, Guid citizenId)
        {
            var incident = await _incidentRepository.GetByIdAsync(id).ConfigureAwait(false);
            
            if (incident == null || incident.Status != IncidentStatus.Draft)
                throw new IncidentNotFoundException();

            if (incident.ReportedById != citizenId)
                throw new CitizenIsNotAnOwnerOfIncidentException();

            incident.Status = IncidentStatus.Submitted;
            await _incidentRepository.UpdateIncidentAsync(incident).ConfigureAwait(false);

            if (incident.Lat != null && incident.Lng != null)
            {
                var inspectorsNearby = await _inspectorSessionService.GetInspectorsNerbyCoordsAsync(incident.Lat.Value, incident.Lng.Value).ConfigureAwait(false);

                if (inspectorsNearby.Any())
                {
                    var notification = new NotificationViewModel()
                    {
                        Title = "Недалеко от вас новое нарушение",
                        MessageType = MessageType.IncidentAround,
                        Body = $"{incident.Address} {incident.Description}",
                        ToId = incident.Id
                    };

                    var result = await _notificationGateway.SendPushNotification(notification, inspectorsNearby.Select(x => x.FirebaseToken)).ConfigureAwait(false);
                    if (!result) Console.WriteLine("Уведомление отправлено с ошибкой");
                }
            }

            return incident;
        }

        public async Task<Incident> AttachFileToIncidentAsync(Guid id, Guid citizenId, IFormFile file, string description)
        {
            var incident = await _incidentRepository.GetByIdAsync(id).ConfigureAwait(false);
            if(incident == null || incident.Status != IncidentStatus.Draft) throw new IncidentNotFoundException();
            if (incident.ReportedById != citizenId) throw new CitizenIsNotAnOwnerOfIncidentException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            var savedFile = await _savedFileService.CreateSavedFileAsync(file.OpenReadStream(), file.FileName, SavedFileType.File).ConfigureAwait(false);
            if (savedFile == null) throw new UploadAttachmentException();

            var newIncidentAttachment = new IncidentAttachment()
            {
                Id = Guid.NewGuid(),
                IncidentId = incident.Id,
                SavedFileId = savedFile.Id,
                Description = description
            };

            await _incidentAttachmentRepository.CreateIncidentAttachmentAsync(newIncidentAttachment).ConfigureAwait(false);

            transaction.Complete();
            transaction.Dispose();

            return await _incidentRepository.GetByIdAsync(id).ConfigureAwait(false);
        }

        public async Task<Incident> AttachIncidentToInspectorAsync(Guid id, Guid inspectorId)
        {
            var incident = await _incidentRepository.GetByIdAsync(id).ConfigureAwait(false);
            if (incident == null || incident.Status != IncidentStatus.Submitted) throw new IncidentNotFoundException();

            incident.UpdatedAt = DateTime.UtcNow;
            incident.Status = IncidentStatus.Attached;
            incident.BindingDate = DateTime.UtcNow;
            incident.InspectorId = inspectorId;

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            await _incidentRepository.UpdateIncidentAsync(incident).ConfigureAwait(false);
            await _inspectorScoreService.CreateScoreAsync(inspectorId, ScoreType.TakeIncident);

            transaction.Complete();
            transaction.Dispose();

            return incident;
        }

        public async Task<Incident> CreateIncidentAsync(Incident incident)
        {
            incident.Id = Guid.NewGuid();
            incident.CreatedAt = DateTime.UtcNow;
            incident.Status = IncidentStatus.Draft;

            await _incidentRepository.CreateIncidentAsync(incident).ConfigureAwait(false);

            return incident;
        }

        public async Task DecreaseInspectorsWithOverdueIncidentsAsync()
        {
            var overdueIncidents = await _incidentRepository.GetOverdueIncidents(_overdueDays).ConfigureAwait(false);

            foreach (var overdueIncident in overdueIncidents)
                if(overdueIncident.InspectorId != null)
                {
                    await _inspectorScoreService.CreateScoreAsync(overdueIncident.InspectorId.Value, ScoreType.OverdueIncident).ConfigureAwait(false);
                    if(overdueIncident.Inspector.TelegramChatId != null)
                        await SendMessage(overdueIncident.Inspector.TelegramChatId, "Были сняты баллы за просроченное нарушение\nЧтобы открыть меню нажмите /start", _telegramToken).ConfigureAwait(false);
                }
        }

        private async Task SendMessage(string chatId, string message, string token)
        {
            string url = $"https://api.telegram.org/bot{token}/sendMessage?" +
                         $"chat_id={chatId}&" +
                         $"text={message}";
            var httpClient = _httpClientFactory.CreateClient();
            var response = await httpClient.GetAsync(new Uri(url)).ConfigureAwait(false);
        }

        public async Task<Incident> DetachFileFromIncidentAsync(Guid id, Guid citizenId)
        {
            var incidentAttachment = await _incidentAttachmentRepository.GetIncidentAttachmentByIdAsync(id).ConfigureAwait(false) ?? throw new IncidentAttachmentNotFoundException();
            var incident = await _incidentRepository.GetByIdAsync(incidentAttachment.IncidentId).ConfigureAwait(false);
            if (incident == null || incident.Status != IncidentStatus.Draft) throw new IncidentNotFoundException();
            if (incident.ReportedById != citizenId || incident.Status != IncidentStatus.Draft) throw new CitizenIsNotAnOwnerOfIncidentException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            await _incidentAttachmentRepository.RemoveIncidentAttachmentAsync(incidentAttachment).ConfigureAwait(false);
            await _savedFileService.RemoveSavedFileAsync(incidentAttachment.SavedFileId).ConfigureAwait(false);

            transaction.Complete();
            transaction.Dispose();

            return await _incidentRepository.GetByIdAsync(id).ConfigureAwait(false);
        }

        public async Task<Incident> GetForInspectorByIdAsync(Guid incidentId, Guid inspectorId)
        {
            var incident = await _incidentRepository.GetByIdAsync(incidentId).ConfigureAwait(false);

            if (incident == null) throw new IncidentNotFoundException();

            incident.ReportedBy.Phone = incident.InspectorId == inspectorId ? incident.ReportedBy.Phone : "";
            return incident;
        }

        public async Task<IEnumerable<Incident>> GetIncidentsAsync(int page, int perPage, IncidentKind? incidentKind, IncidentStatus? incidentStatus, Guid? inspectorId = null)
        {
            return await _incidentRepository.GetIncidentsAsync(page, perPage, incidentKind, incidentStatus, inspectorId).ConfigureAwait(false);
        }

        public async Task<IEnumerable<Incident>> GetIncidentsForCitizenAsync(Citizen citizen, int page, int perPage)
        {
            return await _incidentRepository.GetIncidentsForCitizenAsync(citizen.Id, page, perPage).ConfigureAwait(false);
        }

        public async Task<IEnumerable<Incident>> GetIncidentsNerbyAsync(Inspector inspector, 
                                                                        int page, int perPage, 
                                                                        IncidentKind? incidentKind)
        {
            if (inspector.LastLatLngAt == null) throw new CoordsAreEmptyException();
            if(inspector.LastLatLngAt.Value.AddHours(_maxTimeForCoordsUpdate) < DateTime.UtcNow) throw new CoordsAreTooOldException();

            var incidents = await _incidentRepository.GetIncidentsNerbyCoordsAsync(_maxDistance, 
                                                                            inspector.Lat.Value, inspector.Lng.Value,
                                                                            page, perPage,
                                                                            incidentKind).ConfigureAwait(false);

            return incidents.Select(x => new Incident()
            {
                Id = x.Id,
                Address = x.Address,
                CreatedAt = x.CreatedAt,
                IncidentAttachments = x.IncidentAttachments,
                UpdatedAt = x.UpdatedAt,
                BindingDate = x.BindingDate,
                Description = x.Description,
                Inspector = x.Inspector,
                InspectorId = x.InspectorId,
                Kind = x.Kind,
                Lat = x.Lat,
                Lng = x.Lng,
                ReportedBy = new Citizen()
                {
                    Id = x.ReportedBy.Id,
                    FIO = x.ReportedBy.FIO,
                    Phone = x.InspectorId == inspector.Id ? x.ReportedBy.Phone : ""
                },
                ReportedById = x.ReportedById,
                Status = x.Status,
            });
        }

        public async Task<IncidentStatisticViewModel> GetStatisticAsync()
        {
            var incidentsCount = await _incidentRepository.GetIncidentsStatistic(IncidentStatus.Submitted).ConfigureAwait(false);
            var completedIncidentsCount = await _incidentRepository.GetIncidentsStatistic(IncidentStatus.Completed).ConfigureAwait(false);
            var closedIncidentsCount = await _incidentRepository.GetIncidentsStatistic(IncidentStatus.Closed).ConfigureAwait(false);
            var inWorkIncidentsCount = await _incidentRepository.GetIncidentsStatistic(IncidentStatus.Attached).ConfigureAwait(false);

            return new()
            {
                CountOfCompletedIncidents = completedIncidentsCount + closedIncidentsCount,
                CountOfIncidents = incidentsCount,
                CountOfIncidentsInWork = inWorkIncidentsCount
            };
        }

        public async Task ReportIncident(Guid incidentId, Guid inspectorId, ReportType reportType)
        {
            var incident = await _incidentRepository.GetByIdAsync(incidentId).ConfigureAwait(false) ?? throw new IncidentNotFoundException();
            if (incident.Status != IncidentStatus.Submitted) throw new IncidentNotFoundException();
            var incidentComplaint = await _incidentComplaintRepository.GetIncidentComplaintByInspectorIdAsync(incidentId, inspectorId).ConfigureAwait(false);
            if (incidentComplaint != null) throw new InspectorAlreadyReportIncidentException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            var countOfComplaint = await _incidentComplaintRepository.GetCountOfIncidentComplaintAsync(incidentId).ConfigureAwait(false);

            if (countOfComplaint >= 2)
                incident.Status = IncidentStatus.Deleted;

            incidentComplaint = new IncidentComplaint()
            {
                IncidentId = incidentId,
                InspectorId = inspectorId
            };

            await _incidentComplaintRepository.CreateIncidentComplaintAsync(incidentComplaint).ConfigureAwait(false);

            if (reportType == ReportType.Unacceptable)
            {
                var citizen = incident.ReportedBy;
                
                citizen.Rating = citizen.Rating <= 0.2 ? 0 : citizen.Rating - 0.2;
                await _citizenRepository.UpdateCitizenAsync(citizen).ConfigureAwait(false);
            }

            await _incidentRepository.UpdateIncidentAsync(incident).ConfigureAwait(false);

            transaction.Complete();
            transaction.Dispose();
        }

        public async Task<Incident> UpdateIncidentAsync(Guid id, Guid citizenId, Incident updatedIncident)
        {
            var incident = await _incidentRepository.GetByIdAsync(id).ConfigureAwait(false);
            if (incident == null || incident.Status != IncidentStatus.Draft) throw new IncidentNotFoundException();
            if (incident.ReportedById != citizenId) throw new CitizenIsNotAnOwnerOfIncidentException();

            incident.UpdatedAt = DateTime.UtcNow;
            incident.Kind = updatedIncident.Kind;
            incident.Description = updatedIncident.Description;
            incident.Address = updatedIncident.Address;
            incident.Lat = updatedIncident.Lat;
            incident.Lng = updatedIncident.Lng;

            await _incidentRepository.UpdateIncidentAsync(incident).ConfigureAwait(false);
            return incident;
        }
    }
}
    