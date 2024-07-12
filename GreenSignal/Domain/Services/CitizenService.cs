using Data.Models;
using Data.Repositories;
using Domain.Exceptions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Services
{
    public interface ICitizenService
    {
        Task<Citizen?> GetByIdAsync(Guid id);
        Task<Citizen> GetByPhoneAsync(string phone);
        Task UpsertCitizen(string receivedPhone, string receivedFIO, string? telegramUserId = null);
        Task<Citizen?> GetCitizenByTelegramUserIdAsync(string telegramUserId);
    }

    public class CitizenService : ICitizenService
    {
        private readonly ICitizenRepository _citizenRepository;

        public CitizenService(ICitizenRepository citizenRepository)
        {
            _citizenRepository = citizenRepository;
        }

        public async Task<Citizen?> GetByIdAsync(Guid id)
        {
            var citizen = await _citizenRepository.GetByIdAsync(id).ConfigureAwait(false);

            return citizen;
        }

        public async Task<Citizen> GetByPhoneAsync(string phone)
        {
            var citizen = await _citizenRepository.GetByPhoneAsync(phone).ConfigureAwait(false);

            return citizen ?? throw new CitizenNotFoundException();
        }

        public async Task<Citizen?> GetCitizenByTelegramUserIdAsync(string telegramUserId)
        {
            return await _citizenRepository.GetCitizenByTelegramUserId(telegramUserId).ConfigureAwait(false);
        }

        public async Task UpsertCitizen(string receivedPhone, string receivedFIO, string? telegramUserId = null)
        {
            var citizen = await _citizenRepository.GetByPhoneAsync(receivedPhone).ConfigureAwait(false);
            if (citizen == null)
            {
                await _citizenRepository.CreateCitizenAsync(new Data.Models.Citizen()
                {
                    Id = Guid.NewGuid(),
                    Phone = receivedPhone,
                    FIO = receivedFIO,
                    Rating = 10,
                    TelegramUserId = telegramUserId
                });
            }
            else
            {
                citizen.FIO = receivedFIO;
                if(telegramUserId != null) citizen.TelegramUserId = telegramUserId;
                await _citizenRepository.UpdateCitizenAsync(citizen).ConfigureAwait(false);
            }
        }
    }
}
