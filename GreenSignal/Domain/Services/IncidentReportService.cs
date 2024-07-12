using Data.Models;
using Data.Repositories;
using Domain.Exceptions;
using Microsoft.AspNetCore.Http;
using System.Transactions;
using Domain.ViewModels;
using PdfViews.ViewModels;
using PDFUtility;

namespace Domain.Services
{
    public interface IIncidentReportService
    {
        Task<IEnumerable<IncidentReport>> GetInicdentReportListAsync(Guid inspectorId, int page, int perPage, 
                                                                        Data.Models.IncidentReportKind? incidentReportKind = null, Data.Models.IncidentReportStatus? incidentReportStatus = null);
        Task<IncidentReport> GetIncidentReportByIdAsync(Guid id, Guid inspectorId);
        Task<IncidentReport> CreateIncidentReportAsync(IncidentReport incidentReport, Guid inspectorId);
        Task<IncidentReport> UpdateIncidentReportAsync(Guid id, IncidentReport incidentReport, Guid inspectorId);
        Task RemoveIncidedntReportAsync(Guid id, Guid inspectorId);
        Task<IncidentReport> AttachAttachmentToIncidentReportAsync(Guid id, Guid inspectorId, IFormFile file, string description, DateTime manualDate);
        Task<IncidentReport> DetachAttachmentFromIncidentReportAsync(Guid id, Guid inspectorId);
        Task<IncidentReport> UpdateAttachmentFromIncidentReportAsync(Guid id, Guid inspectorId, string description, DateTime manualDate);
        Task<Stream> GetIncidentReportPdfAsync(Guid id, Guid inspectorId);
        Task<IncidentReportStatisticViewModel> GetStatistic();
    }

    public class IncidentReportService : IIncidentReportService
    {
        private readonly IIncidentReportRepository _incidentReportRepository;
        private readonly IIncidentRepository _incidentRepository;
        private readonly IInspectorRepository _inspectorRepository;
        private readonly ISavedFileService _savedFileService;
        private readonly IIncidentReportAttachmentRepository _incidentReportAttachmentRepository;
        private readonly ILocationRepository _locationRepository;
        private readonly IPdfRender _pdfRender;
        private readonly IIncidentReportAttributeService _incidentReportAttributeService;

        public IncidentReportService(IIncidentReportRepository incidentReportRepository,
                                        IIncidentRepository incidentRepository,
                                        IInspectorRepository inspectorRepository,
                                        ISavedFileService savedFileService,
                                        IIncidentReportAttachmentRepository incidentReportAttachmentRepository,
                                        ILocationRepository locationRepository,
                                        IPdfRender pdfRender,
                                        IIncidentReportAttributeService incidentReportAttributeService)
        {
            _incidentReportRepository = incidentReportRepository;
            _incidentRepository = incidentRepository;
            _inspectorRepository = inspectorRepository;
            _savedFileService = savedFileService;
            _incidentReportAttachmentRepository = incidentReportAttachmentRepository;
            _locationRepository = locationRepository;
            _pdfRender = pdfRender;
            _incidentReportAttributeService = incidentReportAttributeService;
        }

        public async Task<IncidentReport> AttachAttachmentToIncidentReportAsync(Guid id, Guid inspectorId, IFormFile file, string description, DateTime manualDate)
        {
            var incidentReport = await _incidentReportRepository.GetIncidentReportByIdAsync(id).ConfigureAwait(false);
            if (incidentReport == null || incidentReport.Status != Data.Models.IncidentReportStatus.Draft) throw new IncidentReportNotFoundException();
            if (incidentReport.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            var savedFile = await _savedFileService.CreateSavedFileAsync(file.OpenReadStream(), file.FileName, SavedFileType.File).ConfigureAwait(false) ?? throw new UploadAttachmentException();
            var newIncidentAttachment = new IncidentReportAttachment()
            {
                Id = Guid.NewGuid(),
                IncidentReportId = id,
                SavedFileId = savedFile.Id,
                CreatedAt = DateTime.UtcNow,
                Description = description,
                ManualDate = manualDate,
            };

            await _incidentReportAttachmentRepository.CreateIncidentReportAttachmentAsync(newIncidentAttachment).ConfigureAwait(false);

            transaction.Complete();
            transaction.Dispose();

            return await _incidentReportRepository.GetIncidentReportByIdAsync(id).ConfigureAwait(false);
        }

        public async Task<IncidentReport> CreateIncidentReportAsync(IncidentReport incidentReport, Guid inspectorId)
        {
            if(incidentReport.IncidentId != null)
            {
                var incident = await _incidentRepository.GetByIdAsync(incidentReport.IncidentId.Value).ConfigureAwait(false) ?? throw new IncidentReportNotFoundException();
                if (incident.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            }

            if(incidentReport.LocationId != null)
            {
                var location = await _locationRepository.GetLocationByIdAsync(incidentReport.LocationId.Value).ConfigureAwait(false) ?? throw new LocationNotFoundException();
            }
            
            var inspector = await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false) ?? throw new InspectorNotFoundException();
            var countOfReports = await _incidentReportRepository.GetCountOfIncidentReportsByInspectorIdAsync(inspectorId).ConfigureAwait(false);

            incidentReport.Id = Guid.NewGuid();
            incidentReport.CreatedAt = DateTime.UtcNow;
            incidentReport.InspectorId = inspectorId;
            incidentReport.SerialNumber = $"{inspector.Number}-{countOfReports + 1}";
            incidentReport.Status = Data.Models.IncidentReportStatus.Draft;

            await _incidentReportRepository.CreateIncidentReportAsync(incidentReport).ConfigureAwait(false);
            return await _incidentReportRepository.GetIncidentReportByIdAsync(incidentReport.Id).ConfigureAwait(false);
        }

        public async Task<IncidentReport> DetachAttachmentFromIncidentReportAsync(Guid id, Guid inspectorId)
        {
            var incidentReportAttachment = await _incidentReportAttachmentRepository.GetIncidentReportAttachmentByIdAsync(id).ConfigureAwait(false) ?? throw new IncidentReportAttachmentNotFoundException();
            var incidentReport = await _incidentReportRepository.GetIncidentReportByIdAsync(incidentReportAttachment.IncidentReportId).ConfigureAwait(false);

            if (incidentReport == null || incidentReport.Status != Data.Models.IncidentReportStatus.Draft) throw new IncidentReportNotFoundException();
            if (incidentReport.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            await _incidentReportAttachmentRepository.RemoveIncidentReportAttachmentAsync(incidentReportAttachment).ConfigureAwait(false);
            await _savedFileService.RemoveSavedFileAsync(incidentReportAttachment.SavedFileId).ConfigureAwait(false);

            transaction.Complete();
            transaction.Dispose();

            return await _incidentReportRepository.GetIncidentReportByIdAsync(id).ConfigureAwait(false);
        }

        public async Task<IncidentReport> GetIncidentReportByIdAsync(Guid id, Guid inspectorId)
        {
            var incidentReport = await _incidentReportRepository.GetIncidentReportByIdAsync(id).ConfigureAwait(false) ?? throw new IncidentReportNotFoundException();
            if (incidentReport.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            return incidentReport;
        }

        public async Task<IEnumerable<IncidentReport>> GetInicdentReportListAsync(Guid inspectorId, int page, int perPage, 
                                                                                    Data.Models.IncidentReportKind? incidentReportKind = null, Data.Models.IncidentReportStatus? incidentReportStatus = null)
        {
            return await _incidentReportRepository.GetListAsync(inspectorId, page, perPage, incidentReportKind, incidentReportStatus).ConfigureAwait(false);
        }

        public async Task<Stream> GetIncidentReportPdfAsync(Guid id, Guid inspectorId)
        {
            var incidentReport = await _incidentReportRepository.GetIncidentReportByIdAsync(id).ConfigureAwait(false) ?? throw new IncidentReportNotFoundException();
            if (incidentReport.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            var attributes = _incidentReportAttributeService.GetAttributeList(incidentReport.Kind, incidentReport.AttributesVersion);

            var incidentReportModel = new PdfViews.Views.IncidentReportPdfViewModel
            {
                IncidentReport = new IncidentReportViewModel()
                {
                    EndOfInspection = incidentReport.EndOfInspection,
                    IncidentReportAttachements = (await Task.WhenAll(incidentReport.IncidentReportAttachments.Select(async x => new IncidentReportAttachmentViewModel()
                    {
                        Description = x.Description,
                        ManualDate = x.ManualDate,
                        SavedFileBytes = await _savedFileService.GetSavedFileById(x.SavedFileId).ConfigureAwait(false)
                    }))).ToList(),
                    Description = incidentReport.Description,
                    IncidentReportAttributes = incidentReport.IncidentReportAttributes.Select(x => new IncidentReportAttributeViewModel()
                    {
                        Name = x.Name,
                        Title = attributes.FirstOrDefault(attribute => attribute.Name == x.Name)?.Title??x.Name,
                        BoolValue = x.BoolValue,
                        NumberValue = x.NumberValue,
                        StringValue = x.StringValue
                    }).ToList(),
                    Incident = incidentReport.IncidentId == null ? null : new IncidentViewModel()
                    {
                        Address = incidentReport.Incident.Address,
                        CreatedAt = incidentReport.Incident.CreatedAt,
                        BindingDate = incidentReport.Incident.BindingDate,
                        Description = incidentReport.Incident.Description,
                        Kind = (PdfViews.ViewModels.IncidentKind)incidentReport.Incident.Kind,
                        ReportedBy = new CitizenViewModel()
                        {
                            FIO = incidentReport.Incident.ReportedBy.FIO,
                            Phone = incidentReport.Incident.ReportedBy.Phone
                        },
                        Status = (PdfViews.ViewModels.IncidentStatus)incidentReport.Incident.Status,
                    },
                    Inspector = new InspectorViewModel()
                    {
                        FIO = incidentReport.Inspector.FIO,
                        Number = incidentReport.Inspector.Number,
                        Phone = incidentReport.Inspector.Phone,
                        SavedFileBytes = await _savedFileService.GetSavedFileById(incidentReport.Inspector.SignatureId.Value).ConfigureAwait(false),
                        Certificate = incidentReport.Inspector.CertificateId,
                        CertificateDate = incidentReport.Inspector.CertificateDate.Value.ToShortDateString()
                    },
                    Kind = (PdfViews.ViewModels.IncidentReportKind)incidentReport.Kind,
                    Location = incidentReport.Location == null ? null : new LocationViewModel()
                    {
                        Name = incidentReport.Location.Name
                    },
                    ManualDate = incidentReport.ManualDate,
                    SerialNumber = incidentReport.SerialNumber,
                    StartOfInspection = incidentReport.StartOfInspection,
                    Status = (PdfViews.ViewModels.IncidentReportStatus)incidentReport.Status,
                    Address = incidentReport.Address,
                    Lat = incidentReport.Lat,
                    Lng = incidentReport.Lng,
                }
            };

            var actHtml = await _pdfRender.GetHtmlFromRazor("IncidentReportPdfView", incidentReportModel); //Получить html код страницы

            return await _pdfRender.GeneratePdf(actHtml); //Сгенерировать на основе html кода pdf
        }

        public async Task RemoveIncidedntReportAsync(Guid id, Guid inspectorId)
        {
            var incidentReport = await _incidentReportRepository.GetIncidentReportByIdAsync(id).ConfigureAwait(false) ?? throw new IncidentReportNotFoundException();
            if (incidentReport.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            incidentReport.Status = Data.Models.IncidentReportStatus.Archived;
            await _incidentReportRepository.UpdateIncidentReportAsync(incidentReport).ConfigureAwait(false);
        }

        public async Task<IncidentReport> UpdateAttachmentFromIncidentReportAsync(Guid id, Guid inspectorId, string description, DateTime manualDate)
        {
            var incidentReportAttachment = await _incidentReportAttachmentRepository.GetIncidentReportAttachmentByIdAsync(id).ConfigureAwait(false) ?? throw new IncidentReportAttachmentNotFoundException();
            var incidentReport = await _incidentReportRepository.GetIncidentReportByIdAsync(incidentReportAttachment.IncidentReportId).ConfigureAwait(false);

            if (incidentReport == null || incidentReport.Status != Data.Models.IncidentReportStatus.Draft) throw new IncidentReportNotFoundException();
            if (incidentReport.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            incidentReportAttachment.Description = description;
            incidentReportAttachment.ManualDate = manualDate;

            await _incidentReportAttachmentRepository.UpdateIncidentReportAttachment(incidentReportAttachment).ConfigureAwait(false);
            return await _incidentReportRepository.GetIncidentReportByIdAsync(id).ConfigureAwait(false);
        }

        public async Task<IncidentReport> UpdateIncidentReportAsync(Guid id, IncidentReport updatedIncidentReport, Guid inspectorId)
        {
            var incidentReport = await _incidentReportRepository.GetIncidentReportByIdAsync(id).ConfigureAwait(false) ?? throw new IncidentReportNotFoundException();

            if (updatedIncidentReport.IncidentId != null)
            {
                var incident = await _incidentRepository.GetByIdAsync(updatedIncidentReport.IncidentId.Value).ConfigureAwait(false) ?? throw new IncidentNotFoundException();
                if (incident.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            }

            if (incidentReport.LocationId != null)
            {
                var location = await _locationRepository.GetLocationByIdAsync(incidentReport.LocationId.Value).ConfigureAwait(false) ?? throw new LocationNotFoundException();
            }

            incidentReport.ManualDate = updatedIncidentReport.ManualDate;
            incidentReport.Kind = updatedIncidentReport.Kind;
            incidentReport.StartOfInspection = updatedIncidentReport.StartOfInspection;
            incidentReport.EndOfInspection = updatedIncidentReport.EndOfInspection;
            incidentReport.IncidentId = updatedIncidentReport.IncidentId;
            incidentReport.Lat = updatedIncidentReport.Lat;
            incidentReport.Lng = updatedIncidentReport.Lng;
            incidentReport.AttributesVersion = updatedIncidentReport.AttributesVersion;
            incidentReport.Description = updatedIncidentReport.Description;

            await _incidentReportRepository.UpdateIncidentReportAsync(incidentReport).ConfigureAwait(false);
            return await _incidentReportRepository.GetIncidentReportByIdAsync(id).ConfigureAwait(false);
        }

        public async Task<IncidentReportStatisticViewModel> GetStatistic()
        {
            var incidentReportsCount = await _incidentReportRepository.GetSentIncidentReportCount().ConfigureAwait(false);
            return new()
            {
                CountOfSentIncidentReports = incidentReportsCount
            };
        }
    }
}
