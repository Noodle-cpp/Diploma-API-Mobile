using Api.ViewModels.Responses;
using AutoMapper;
using Data.Models;

namespace Api.ViewModels.Profiles
{
    public class ReceiveMessageProfile : Profile
    {
        public ReceiveMessageProfile()
        {
            CreateMap<ReceiveMessage, ReceiveMessageViewModel>();
            CreateMap<MessageAttachment, MessageAttachmentViewModel>();
        }
    }
}
