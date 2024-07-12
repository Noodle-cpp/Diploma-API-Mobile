using Api.ViewModels.Requests;
using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;

namespace Api.ViewModels.Profiles
{
    public class IncidentReportProfile : Profile
    {
        public IncidentReportProfile()
        {
            CreateMap<CreateIncidentReportViewModel, IncidentReport>();
            CreateMap<IncidentReport, IncidentReportViewModel>();
            CreateMap<UpdateIncidentReportViewModel, IncidentReport>();
            CreateMap<Domain.ViewModels.IncidentReportStatisticViewModel, ViewModels.Responses.IncidentReportStatisticViewModel>();
        }
    }
}
