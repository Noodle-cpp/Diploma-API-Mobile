using Data.Models;
using Data.Repositories;
using Domain.Exceptions;
using Infrastructure;
using MailManager;
using MailManager.ViewModels;
using Microsoft.AspNetCore.Http;
using Microsoft.OpenApi.Extensions;
using PDFUtility;
using System.Transactions;

namespace Domain.Services
{
    public interface IPetitionService
    {
        Task<IEnumerable<Petition>> GetPetitionListAsync(Guid inspectorId, int page, int perPage);
        Task<Petition> GetPetitionByIdAsync(Guid id, Guid inspectorId);
        Task<Petition> CreatePetitionAsync(Petition petition, Guid inspectorId);
        Task<Petition> UpdatePetitionAsync(Guid id, Petition updatedPetition, Guid inspectorId);
        Task<Petition> ClosePetitionAsync(Guid id, Guid inspectorId, bool isSuccess);
        Task RemovePetitionAsync(Guid id, Guid inspectorId);
        Task<Petition> PetitionAttachmentAdd(Guid inspectorId, Guid petitionId, IFormFile file, string description,
                                                double? lat, double? lng, DateTime manualDate, DateTime createdAt);
        Task<Petition> PetitionAttachmentRemove(Guid attachmentId, Guid inspectorId);
        Task<Petition> AttachIncidentReportToPetitionAsync(Guid id, Guid incidentReportId, Guid inspectorId);
        Task<Petition> DetachIncidentReportFromPetitionAsync(Guid id, Guid inspectorId);
        Task<Petition> AttachPetitionToParent(Guid id, Guid parentId, Guid inspectorId);
        Task<Petition> DetachParentFromPetitionAsync(Guid id, Guid inspectorId);
        Task<Stream> GetPetitionPdfAsync(Guid id, Guid inspectorId);
        Task SentPetitionAsync(Guid id, Guid inspectorId);
    }

    public class PetitionService : IPetitionService
    {
        private readonly IPetitionRepository _petitionRepository;
        private readonly IIncidentReportRepository _incidentReportRepository;
        private readonly IDepartmentRepository _departmentRepository;
        private readonly ISavedFileService _savedFileService;
        private readonly IPetitionAttachmentRepository _petitionAttachmentRepository;
        private readonly IPdfRender _pdfRender;
        private readonly IInspectorScoreService _inspectorScoreService;
        private readonly IPetitionAttributeService _petitionAttributeService;
        private readonly IIncidentReportAttributeService _incidentReportAttributeService;
        private readonly IInspectorRepository _inspectorRepository;
        private readonly IIncidentRepository _incidentRepository;
        private readonly IMailManagerService _mailManagerService;

        public PetitionService(IPetitionRepository petitionRepository,
                                IIncidentReportRepository incidentReportRepository,
                                IDepartmentRepository departmentRepository,
                                ISavedFileService savedFileService,
                                IPetitionAttachmentRepository petitionAttachmentRepository,
                                IPdfRender pdfRender,
                                IInspectorScoreService inspectorScoreService,
                                IPetitionAttributeService petitionAttributeService,
                                IInspectorRepository inspectorRepository,
                                IIncidentRepository incidentRepository,
                                IIncidentReportAttributeService incidentReportAttributeService,
                                IMailManagerService mailManagerService)
        {
            _petitionRepository = petitionRepository;
            _incidentReportRepository = incidentReportRepository;
            _departmentRepository = departmentRepository;
            _savedFileService = savedFileService;
            _petitionAttachmentRepository = petitionAttachmentRepository;
            _pdfRender = pdfRender;
            _inspectorScoreService = inspectorScoreService;
            _petitionAttributeService = petitionAttributeService;
            _inspectorRepository = inspectorRepository;
            _incidentRepository = incidentRepository;
            _incidentReportAttributeService = incidentReportAttributeService;
            _mailManagerService = mailManagerService;
        }

        public async Task<Petition> AttachIncidentReportToPetitionAsync(Guid id, Guid incidentReportId, Guid inspectorId)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
            if (petition == null || petition.Status == PetitionStatus.Archived) throw new PetitionNotFoundException();
            if (petition.Status == PetitionStatus.Success || petition.Status == PetitionStatus.Failed) throw new PetitionIsAlreadyCloseException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            //if (petition.IncidentReportId != null || petition.ParentPetitionId != null) throw new PetitionAlreadyHasAParentException();

            var incidentReport = await _incidentReportRepository.GetIncidentReportByIdAsync(incidentReportId).ConfigureAwait(false);
            if (incidentReport == null || incidentReport.Status == Data.Models.IncidentReportStatus.Archived) throw new IncidentReportNotFoundException();
            if (incidentReport.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            var isIncidentReportAtatched = await _incidentReportRepository.IsIncidentReportAttached(incidentReportId).ConfigureAwait(false);
            if (isIncidentReportAtatched) throw new ParentIsAlreadyAttachedException();

            petition.ParentPetitionId = null;
            petition.IncidentReportId = incidentReportId;
            await _petitionRepository.UpdatePetitionAsync(petition).ConfigureAwait(false);
            return await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
        }

        public async Task<Petition> AttachPetitionToParent(Guid id, Guid parentId, Guid inspectorId)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
            if (petition == null || petition.Status == PetitionStatus.Archived) throw new PetitionNotFoundException();
            if (petition.Status == PetitionStatus.Success || petition.Status == PetitionStatus.Failed) throw new PetitionIsAlreadyCloseException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            //if (petition.IncidentReportId != null || petition.ParentPetitionId != null) throw new PetitionAlreadyHasAParentException();
            if (id == parentId) throw new PetitionCantBeAParentOfHimselfException();

            var parent = await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
            if (parent == null || parent.Status == PetitionStatus.Success || parent.Status == PetitionStatus.Failed) throw new PetitionNotFoundException();
            if (parent.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            var isParentAttached = await _petitionRepository.IsPetitionAttached(parentId).ConfigureAwait(false);
            if (isParentAttached) throw new ParentIsAlreadyAttachedException();

            petition.IncidentReportId = null;
            petition.ParentPetitionId = parentId;
            await _petitionRepository.UpdatePetitionAsync(petition).ConfigureAwait(false);
            return await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
        }

        public async Task<Petition> ClosePetitionAsync(Guid id, Guid inspectorId, bool isSuccess)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
            if (petition == null || petition.Status == PetitionStatus.Archived) throw new PetitionNotFoundException();
            if (petition.Status != PetitionStatus.Sent && petition.Status != PetitionStatus.Replied) throw new PetitionWasNotSentToADepartmentException();
            if (petition.Status == PetitionStatus.Success || petition.Status == PetitionStatus.Failed) throw new PetitionIsAlreadyCloseException();
            if(petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            if (petition.ParentPetitionId != null)
            {
                var parentPetition = await _petitionRepository.GetPetitionByIdAsync(petition.ParentPetitionId.Value).ConfigureAwait(false) ?? throw new ParentPetitionNotFoundException();
                parentPetition.Status = isSuccess ? PetitionStatus.Success : PetitionStatus.Failed;
                await _petitionRepository.UpdatePetitionAsync(parentPetition).ConfigureAwait(false);
            }
            else if (petition.IncidentReportId != null)
            {
                var incidentReport = await _incidentReportRepository.GetIncidentReportByIdAsync(petition.IncidentReportId.Value).ConfigureAwait(false) ?? throw new IncidentReportNotFoundException();

                incidentReport.Status = isSuccess ? IncidentReportStatus.Completed_successfuly : IncidentReportStatus.Completed_unsucessful;
                await _incidentReportRepository.UpdateIncidentReportAsync(incidentReport).ConfigureAwait(false);

                if(incidentReport.IncidentId != null)
                {
                    var incident = await _incidentRepository.GetByIdAsync(incidentReport.IncidentId.Value).ConfigureAwait(false) ?? throw new IncidentNotFoundException();
                    incident.Status = isSuccess ? IncidentStatus.Completed : IncidentStatus.Closed;
                    await _incidentRepository.UpdateIncidentAsync(incident).ConfigureAwait(false);
                }
            }

            petition.Status = isSuccess ? PetitionStatus.Success : PetitionStatus.Failed;
            await _petitionRepository.UpdatePetitionAsync(petition).ConfigureAwait(false);

            await _inspectorScoreService.CreateScoreAsync(inspectorId, ScoreType.PetitionClose);

            transaction.Complete();
            transaction.Dispose();

            return await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
        }

        public async Task<Petition> CreatePetitionAsync(Petition petition, Guid inspectorId)
        {
            var department = await _departmentRepository.GetDepartmentByIdAsync(petition.DepartmentId).ConfigureAwait(false) ?? throw new DepartmentNotFoundException();
            if (!department.IsActive) throw new DepartmentNotFoundException();

            var inspector = await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false) ?? throw new InspectorNotFoundException();

            if (petition.ParentPetitionId != null)
            {
                var parentPetition = await _petitionRepository.GetPetitionByIdAsync(petition.ParentPetitionId.Value).ConfigureAwait(false) ?? throw new ParentPetitionNotFoundException();
                if (parentPetition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            }
            else if(petition.IncidentReportId != null)
            {
                var incidentReport = await _incidentReportRepository.GetIncidentReportByIdAsync(petition.IncidentReportId.Value).ConfigureAwait(false) ?? throw new IncidentReportNotFoundException();
                if (incidentReport.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

                incidentReport.Status = IncidentReportStatus.Sent;
                await _incidentReportRepository.UpdateIncidentReportAsync(incidentReport).ConfigureAwait(false);
            }
            else throw new PetitionMustHaveAParentException();

            petition.Id = Guid.NewGuid();
            var petitionNumber = await _petitionRepository.GetPetitionsCountAsync().ConfigureAwait(false) + 1;
            petition.SerialNumber = $"{inspector.Number}-{petitionNumber}";
            petition.InspectorId = inspectorId;
            petition.CreatedAt = DateTime.UtcNow;

            await _petitionRepository.CreatePetitionAsync(petition).ConfigureAwait(false);

            return await _petitionRepository.GetPetitionByIdAsync(petition.Id).ConfigureAwait(false);
        }

        public async Task<Petition> DetachIncidentReportFromPetitionAsync(Guid id, Guid inspectorId)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
            if (petition == null || petition.Status == PetitionStatus.Archived) throw new PetitionNotFoundException();
            if (petition.Status == PetitionStatus.Success || petition.Status == PetitionStatus.Failed) throw new PetitionIsAlreadyCloseException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            petition.IncidentReportId = null;
            await _petitionRepository.UpdatePetitionAsync(petition).ConfigureAwait(false);
            return await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
        }

        public async Task<Petition> DetachParentFromPetitionAsync(Guid id, Guid inspectorId)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
            if (petition == null || petition.Status == PetitionStatus.Archived) throw new PetitionNotFoundException();
            if (petition.Status == PetitionStatus.Success || petition.Status == PetitionStatus.Failed) throw new PetitionIsAlreadyCloseException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            petition.ParentPetitionId = null;
            await _petitionRepository.UpdatePetitionAsync(petition).ConfigureAwait(false);
            return await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
        }

        public async Task<Stream> GetPetitionPdfAsync(Guid id, Guid inspectorId)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false) ?? throw new PetitionNotFoundException();
            Guid? petitionId = petition.Id;
            IncidentReport? incidentReport = null;
            while (incidentReport == null)
            {
                var parentPetition = await _petitionRepository.GetPetitionByIdAsync(petitionId.Value).ConfigureAwait(false);
                if (parentPetition.IncidentReportId != null)
                    incidentReport = await _incidentReportRepository.GetIncidentReportByIdAsync(parentPetition.IncidentReportId.Value).ConfigureAwait(false);
                petitionId = parentPetition.ParentPetitionId;
            }

            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            var attributes = _petitionAttributeService.GetAttributeList(petition.Kind, petition.AttributeVersion);
            var incidentReportAttributes = _incidentReportAttributeService.GetAttributeList(incidentReport.Kind, incidentReport.AttributesVersion);

            var PetitionModel = new PdfViews.Views.PetitionPdfViewModel
            {
                Petition = new PdfViews.ViewModels.PetitionViewModel()
                {
                    Id = id,
                    Attachements = (await Task.WhenAll(petition.Attachments.Select(async x => new PdfViews.ViewModels.PetitionAttachmentViewModel()
                    {
                        Id = x.Id,
                        CreatedAt = x.CreatedAt,
                        Description = x.Description,
                        ManualDate = x.ManualDate,
                        PetitionId = x.PetitionId,
                        SavedFileBytes = await _savedFileService.GetSavedFileById(x.SavedFileId).ConfigureAwait(false),
                        SavedFileId = x.Id
                    }))),
                    Attributes = petition.Attributes.Select(x => new PdfViews.ViewModels.PetitionAttributeViewModel()
                    {
                        Id = x.Id,
                        BoolValue = x.BoolValue,
                        Name = x.Name,
                        Title = attributes.FirstOrDefault(attribute => attribute.Name == x.Name)?.Title ?? x.Name,
                        NumberValue = x.NumberValue,
                        StringValue = x.StringValue
                    }),
                    AttributeVersion = petition.AttributeVersion,
                    Date = petition.Date,
                    Department = new PdfViews.ViewModels.DepartmentViewModel()
                    {
                        Id = petition.Department.Id,
                        Address = petition.Department.Address,
                        AliasNames = petition.Department.AliasNames,
                        IsActive = petition.Department.IsActive,
                        Email = petition.Department.Email,
                        Location = new PdfViews.ViewModels.LocationViewModel()
                        {
                            Name = petition.Department.Location.Name,
                        },
                        LocationId = petition.Department.LocationId,
                        Name = petition.Department.Name,
                    },
                    DepartmentId = petition.DepartmentId,
                    IncidentReportId = petition.IncidentReportId,
                    Inspector = new PdfViews.ViewModels.InspectorViewModel()
                    {
                        FIO = petition.Inspector.FIO,
                        Number = petition.Inspector.Number,
                        Phone = petition.Inspector.Phone
                    },
                    InspectorId = petition.InspectorId,
                    Kind = (PdfViews.ViewModels.PetitionKind)petition.Kind,
                    ReceiveMessages = await Task.WhenAll(petition.ReceiveMessages.Select(async x => new PdfViews.ViewModels.ReceiveMessageViewModel()
                    {
                        FromAddress = x.FromAddress,
                        MessageAttachments = (await Task.WhenAll(x.MessageAttachments.Select(async x => new PdfViews.ViewModels.MessageAttachmentViewModel()
                        {
                            Id = x.Id,
                            SavedFileBytes = await _savedFileService.GetSavedFileById(x.SavedFileId).ConfigureAwait(false)
                        })).ConfigureAwait(false)).ToList(),
                        Content = x.Content,
                        FromName = x.FromName,
                        Id = x.Id,
                        Seen = x.Seen,
                        Subject = x.Subject
                    })).ConfigureAwait(false),
                    SerialNumber = petition.SerialNumber,
                    Status = (PdfViews.ViewModels.PetitionStatus)petition.Status,
                },

                IncidentReport = new PdfViews.ViewModels.IncidentReportViewModel()
                {
                    EndOfInspection = incidentReport.EndOfInspection,
                    IncidentReportAttachements = (await Task.WhenAll(incidentReport.IncidentReportAttachments.Select(async x => new PdfViews.ViewModels.IncidentReportAttachmentViewModel()
                    {
                        Description = x.Description,
                        ManualDate = x.ManualDate,
                        SavedFileBytes = await _savedFileService.GetSavedFileById(x.SavedFileId).ConfigureAwait(false)
                    }))).ToList(),
                    Description = incidentReport.Description,
                    IncidentReportAttributes = incidentReport.IncidentReportAttributes.Select(x => new PdfViews.ViewModels.IncidentReportAttributeViewModel()
                    {
                        Name = x.Name,
                        Title = incidentReportAttributes.FirstOrDefault(attribute => attribute.Name == x.Name)?.Title??x.Name,
                        BoolValue = x.BoolValue,
                        NumberValue = x.NumberValue,
                        StringValue = x.StringValue
                    }).ToList(),
                    Incident = incidentReport.Incident == null ? null : new PdfViews.ViewModels.IncidentViewModel()
                    {
                        Address = incidentReport.Incident.Address,
                        CreatedAt = incidentReport.Incident.CreatedAt,
                        BindingDate = incidentReport.Incident.BindingDate,
                        Description = incidentReport.Incident.Description,
                        Kind = (PdfViews.ViewModels.IncidentKind)incidentReport.Incident.Kind,
                        ReportedBy = new PdfViews.ViewModels.CitizenViewModel()
                        {
                            FIO = incidentReport.Incident.ReportedBy.FIO,
                            Phone = incidentReport.Incident.ReportedBy.Phone
                        },
                        Status = (PdfViews.ViewModels.IncidentStatus)incidentReport.Incident.Status,
                    },
                    Inspector = new PdfViews.ViewModels.InspectorViewModel()
                    {
                        FIO = incidentReport.Inspector.FIO,
                        Number = incidentReport.Inspector.Number,
                        Phone = incidentReport.Inspector.Phone,
                        SavedFileBytes = await _savedFileService.GetSavedFileById(incidentReport.Inspector.SignatureId.Value).ConfigureAwait(false),
                        Certificate = incidentReport.Inspector.CertificateId,
                        CertificateDate = incidentReport.Inspector.CertificateDate.Value.ToShortDateString()
                    },
                    Kind = (PdfViews.ViewModels.IncidentReportKind)incidentReport.Kind,
                    Location = incidentReport.Location == null ? null : new PdfViews.ViewModels.LocationViewModel()
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

            var actHtml = await _pdfRender.GetHtmlFromRazor("PetitionPdfView", PetitionModel); //Получить html код страницы
            return await _pdfRender.GeneratePdf(actHtml); //Сгенерировать на основе html кода pdf
        }

        public async Task<Petition> GetPetitionByIdAsync(Guid id, Guid inspectorId)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
            if (petition == null || petition.Status == PetitionStatus.Archived) throw new PetitionNotFoundException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            return petition;
        }

        public async Task<IEnumerable<Petition>> GetPetitionListAsync(Guid inspectorId, int page, int perPage)
        {
            return await _petitionRepository.GetPetitionListAsync(inspectorId, page, perPage).ConfigureAwait(false);
        }

        public async Task<Petition> PetitionAttachmentAdd(Guid inspectorId, Guid petitionId, IFormFile file, string description, 
                                                    double? lat, double? lng, DateTime manualDate, DateTime createdAt)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(petitionId).ConfigureAwait(false);
            if (petition == null || petition.Status == PetitionStatus.Archived) throw new PetitionNotFoundException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            var savedFile = await _savedFileService.CreateSavedFileAsync(file.OpenReadStream(), file.FileName, SavedFileType.File).ConfigureAwait(false) ?? throw new UploadAttachmentException();

            var newPetitionAttachment = new PetitionAttachment()
            {
                Id = Guid.NewGuid(),
                PetitionId = petitionId,
                SavedFileId = savedFile.Id,
                CreatedAt = createdAt,
                Description = description,
                ManualDate = manualDate,
            };

            await _petitionAttachmentRepository.CreatePetitionAttachmentAsync(newPetitionAttachment).ConfigureAwait(false);

            transaction.Complete();
            transaction.Dispose();

            return await _petitionRepository.GetPetitionByIdAsync(petitionId).ConfigureAwait(false);
        }

        public async Task<Petition> PetitionAttachmentRemove(Guid attachmentId, Guid inspectorId)
        {
            var petitionAttachment = await _petitionAttachmentRepository.GetPetitionAttachmentByIdAsync(attachmentId).ConfigureAwait(false) ?? throw new PetitionAttachmentNotFoundException();
            var petition = await _petitionRepository.GetPetitionByIdAsync(petitionAttachment.PetitionId).ConfigureAwait(false) ?? throw new PetitionNotFoundException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            await _petitionAttachmentRepository.RemovePetitionAttachmentAsync(petitionAttachment).ConfigureAwait(false);
            await _savedFileService.RemoveSavedFileAsync(petitionAttachment.SavedFileId).ConfigureAwait(false);

            transaction.Complete();
            transaction.Dispose();

            return await _petitionRepository.GetPetitionByIdAsync(petition.Id).ConfigureAwait(false);
        }

        public async Task RemovePetitionAsync(Guid id, Guid inspectorId)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
            if (petition == null || petition.Status == PetitionStatus.Archived) throw new PetitionNotFoundException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            petition.Status = PetitionStatus.Archived;
            await _petitionRepository.UpdatePetitionAsync(petition).ConfigureAwait(false);
        }

        public async Task<Petition> UpdatePetitionAsync(Guid id, Petition updatedPetition, Guid inspectorId)
        {
            var petition = await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false) ?? throw new PetitionNotFoundException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            if (petition.ParentPetitionId != null)
            {
                var parentPetition = await _petitionRepository.GetPetitionByIdAsync(petition.ParentPetitionId.Value).ConfigureAwait(false) ?? throw new ParentPetitionNotFoundException();
                if (parentPetition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            }
            else if (petition.IncidentReportId != null)
            {
                var incidentReports = await _incidentReportRepository.GetIncidentReportByIdAsync(petition.IncidentReportId.Value).ConfigureAwait(false) ?? throw new IncidentReportNotFoundException();
                if (incidentReports.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();
            }
            else throw new PetitionMustHaveAParentException();

            var department = await _departmentRepository.GetDepartmentByIdAsync(petition.DepartmentId).ConfigureAwait(false) ?? throw new DepartmentNotFoundException();
            if (!department.IsActive) throw new DepartmentNotFoundException();

            petition.DepartmentId = updatedPetition.DepartmentId;
            petition.ParentPetitionId = updatedPetition.ParentPetitionId;
            petition.IncidentReportId = updatedPetition.IncidentReportId;
            petition.Kind = updatedPetition.Kind;
            petition.AttributeVersion = updatedPetition.AttributeVersion;
            petition.Date = updatedPetition.Date;

            await _petitionRepository.UpdatePetitionAsync(petition).ConfigureAwait(false);
            return await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false);
        }

        public async Task SentPetitionAsync(Guid id, Guid inspectorId)
        {
            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);
            var inspector = await _inspectorRepository.GetByIdAsync(inspectorId).ConfigureAwait(false) ?? throw new InspectorNotFoundException();

            var petition = await _petitionRepository.GetPetitionByIdAsync(id).ConfigureAwait(false) ?? throw new PetitionNotFoundException();
            if (petition.InspectorId != inspectorId) throw new InspectorNotAnOwnerException();

            var department = await _departmentRepository.GetDepartmentByIdAsync(petition.DepartmentId).ConfigureAwait(false) ?? throw new DepartmentNotFoundException();
            if (!department.IsActive) throw new DepartmentNotFoundException();

            string kind = "";
            switch (petition.Kind)
            {
                case PetitionKind.AirPollution:
                    kind = "Загярзнение воздуха";
                    break;
                case PetitionKind.SoilPollution:
                    kind = "Загрязнение почвы";
                    break;
                case PetitionKind.Excavation:
                    kind = "Добыча недр";
                    break;
                case PetitionKind.Dump:
                    kind = "Свалка";
                    break;
                case PetitionKind.TreeCutting:
                    kind = "Вырубка деревьев";
                    break;
                case PetitionKind.Radiation:
                    kind = "Радиация";
                    break;
                default:
                    break;
            }

            using (var file = await GetPetitionPdfAsync(petition.Id, inspectorId).ConfigureAwait(false))
            {
                file.Position = 0;
                await _mailManagerService.SendMessage(new MailManager.ViewModels.EmailMessageViewModel
                {
                    FromAddress = new()
                    {
                        Address = inspector.InternalEmail,
                        Name = inspector.FIO
                    },
                    ToAddress = new()
                    {
                        Address = department.Address,
                        Name = department.Name
                    },
                    Subject = $"Обращение №{petition.SerialNumber} - {kind}",
                    Attachments = [
                    new()
                    {
                        Data = ReadFully(file),
                        OriginalName = $"Обращение №{petition.SerialNumber}.pdf"
                    }
                    ],
                    Content = $"Я, {inspector.FIO}, направляю вам обращение по типу {kind}"
                }).ConfigureAwait(false);
            }

            petition.Status = PetitionStatus.Sent;

            await _petitionRepository.UpdatePetitionAsync(petition).ConfigureAwait(false);

            transaction.Complete();
            transaction.Dispose();
        }

        private static byte[] ReadFully(Stream input)
        {
            byte[] buffer = new byte[16 * 1024];
            using (MemoryStream ms = new MemoryStream())
            {
                int read;
                while ((read = input.Read(buffer, 0, buffer.Length)) > 0)
                {
                    ms.Write(buffer, 0, read);
                }
                return ms.ToArray();
            }
        }
    }
}
