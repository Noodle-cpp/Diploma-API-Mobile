using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;

namespace Api.ViewModels.Profiles
{
    public class SavedFileProfile : Profile
    {
        public SavedFileProfile()
        {
            CreateMap<SavedFile, SavedFileViewModel>();
        }
    }
}
