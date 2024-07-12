using Data.Models;
using Data.Repositories;
using Domain.AttributeServices;
using Domain.AttributeServices.Models;
using Domain.Exceptions;
using Domain.ViewModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Services
{
    public interface IPetitionAttributeService
    {
        public Task CreateAttributeList(IEnumerable<AttributeViewModel> attributesVM, Petition petition);
        HashSet<PetitionAttributeItem> GetAttributeList(PetitionKind kind, int attributeVersion);
    }

    public class PetitionAttributeService : IPetitionAttributeService
    {
        private readonly IPetitionAttributeRepository _petitionAttributeRepository;
        private readonly IPetitionAttributeItems _petitionAttributeItems;
        private readonly IPetitionAttributeValue _attributeValue;

        public PetitionAttributeService(IPetitionAttributeRepository petitionAttributeRepository,
                                        IPetitionAttributeItems petitionAttributeItems,
                                        IPetitionAttributeValue attributeValue)
        {
            _petitionAttributeRepository = petitionAttributeRepository;
            _petitionAttributeItems = petitionAttributeItems;
            _attributeValue = attributeValue;
        }

        public async Task CreateAttributeList(IEnumerable<AttributeViewModel> attributesVM, Petition petition)
        {
            var localAttributes = _petitionAttributeItems.GetAttributesHashSet(petition.Kind, petition.AttributeVersion);
            List<AttributeViewModel> attributesViewModel = new();

            foreach (var localAttribute in localAttributes)
            {
                var attribute = attributesVM.FirstOrDefault(x => x.Name == localAttribute.Name);
                Console.WriteLine(attribute.Name);
                AttributeRequiredCheck(attribute, localAttribute.IsRequired, localAttribute.Type.Name, localAttribute.Name);

                if (attribute != null) attributesViewModel.Add(_attributeValue.CreateAttribute(localAttribute, attribute));
            }

            var attributes = await _petitionAttributeRepository.GetListOfAttributesByPetitionIdAsync(petition.Id).ConfigureAwait(false);
            await _petitionAttributeRepository.RemoveListOfAttributesAsync(attributes).ConfigureAwait(false);
            await _petitionAttributeRepository.CreateRangeAttributes(CreateRangePetitionAttributes(attributesViewModel, petition.Id));
        }

        private IEnumerable<PetitionAttribute> CreateRangePetitionAttributes(IEnumerable<AttributeViewModel> createPetitionAttributes, Guid id)
        {
            return createPetitionAttributes.Select(x => new PetitionAttribute()
            {
                Id = $"{id}-{x.Name}",
                BoolValue = x.BoolValue,
                PetitionId = id,
                Name = x.Name,
                NumberValue = x.NumberValue,
                StringValue = x.StringValue
            });
        }

        private static void AttributeRequiredCheck(AttributeViewModel attribute, bool isRequired, string type, string name)
        {
            if (attribute == null && isRequired)
                throw new AttributeIsRequiredException($"{type}.{name} is required");
        }

        public HashSet<PetitionAttributeItem> GetAttributeList(PetitionKind kind, int attributeVersion)
        {
            return _petitionAttributeItems.GetAttributesHashSet(kind, attributeVersion);
        }
    }
}
