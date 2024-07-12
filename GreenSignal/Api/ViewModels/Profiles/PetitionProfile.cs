using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;

namespace Api.ViewModels.Profiles
{
    public class PetitionProfile : Profile
    {
        public PetitionProfile()
        {
            CreateMap<Petition, PetitionViewModel>();
            CreateMap<CreatePetitionViewModel, Petition>();
            CreateMap<UpdatePetitionViewModel, Petition>();
        }
    }
}
