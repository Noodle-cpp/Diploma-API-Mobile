using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;

namespace Api.ViewModels.Profiles
{
    public class PetitionAttachmentProfile : Profile
    {
        public PetitionAttachmentProfile()
        {
            CreateMap<PetitionAttachment, PetitionAttachmentViewModel>();
        }
    }
}
