using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;
using Domain.ViewModels;

namespace Api.ViewModels.Profiles
{
    public class PetitionAttributeProfile : Profile
    {
        public PetitionAttributeProfile()
        {
            CreateMap<PetitionAttribute, PetitionAttributeViewModel>();
            CreateMap<CreatePetitionAttributeViewModel, AttributeViewModel>();
        }
    }
}
