using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;

namespace Api.ViewModels.Profiles
{
    public class CitizenProfile : Profile
    {
        public CitizenProfile()
        {
            CreateMap<Citizen, CitizenViewModel>();
        }
    }
}
