using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;

namespace Api.ViewModels.Profiles
{
    public class IncidentAttachmentProfile : Profile
    {
        public IncidentAttachmentProfile()
        {
            CreateMap<IncidentAttachment, IncidentAttachmentViewModel>();
        }
    }
}
