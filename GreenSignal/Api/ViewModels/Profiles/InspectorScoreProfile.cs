using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;

namespace Api.ViewModels.Profiles
{
    public class InspectorScoreProfile : Profile
    {
        public InspectorScoreProfile()
        {
            CreateMap<Domain.ViewModels.InspectorRatingScore, Api.ViewModels.Responses.InspectorRatingScore>();
            CreateMap<InspectorScore, InspectorScoreViewModel>();
        }
    }
}
