using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;
using Domain.ViewModels;

namespace Api.ViewModels.Profiles
{
    public class IncidentReportAttributeProfile : Profile
    {
        public IncidentReportAttributeProfile()
        {
            CreateMap<CreateIncidentReportAttributeViewModel, AttributeViewModel>();
            CreateMap<IncidentReportAttribute, IncidentReportAttributeViewModel>();
        }
    }
}
