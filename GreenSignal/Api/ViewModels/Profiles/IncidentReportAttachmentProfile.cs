using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;

namespace Api.ViewModels.Profiles
{
    public class IncidentReportAttachmentProfile : Profile
    {
        public IncidentReportAttachmentProfile()
        {
            CreateMap<IncidentReportAttachment, IncidentReportAttachmentViewModel>();
            CreateMap<CreateIncidentReportAttachmentViewModel, IncidentReportAttachment>();
            CreateMap<UpdateIncidentReportAttachmentViewModel, IncidentReportAttachment>();
        }
    }
}
