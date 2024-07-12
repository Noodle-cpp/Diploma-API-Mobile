using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;

namespace Api.ViewModels.Profiles
{
    public class IncidentProfile : Profile
    {
        public IncidentProfile()
        {
            CreateMap<Incident, IncidentViewModel>();
            CreateMap<CreateIncidentViewModel, Incident>();
            CreateMap<UpdateIncidentViewModel, Incident>();
            CreateMap<Domain.ViewModels.IncidentStatisticViewModel, ViewModels.Responses.IncidentStatisticViewModel>();
        }
    }
}
