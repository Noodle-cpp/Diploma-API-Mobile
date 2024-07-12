using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;

namespace Api.ViewModels.Profiles
{
    public class InspectorProfile : Profile
    {
        public InspectorProfile()
        {
            CreateMap<Inspector, InspectorViewModel>();
            CreateMap<CreateInspectorViewModel, Inspector>();
            CreateMap<UpdateInspectorViewModel, Inspector>();
            CreateMap<Inspector, InspectorInformationViewModel>();
            CreateMap<Inspector, InspectorForIncidentViewModel>();
            CreateMap<InspectorSession, InspectorSessionViewModel>();
        }
    }
}
