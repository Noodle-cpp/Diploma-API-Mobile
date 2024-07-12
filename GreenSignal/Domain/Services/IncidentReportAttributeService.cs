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
    public interface IIncidentReportAttributeService
    {
        public Task CreateAttributeList(IEnumerable<AttributeViewModel> attributesVM, IncidentReport incidentReport);
        public HashSet<IncidentReportAttributeItem> GetAttributeList(IncidentReportKind kind, int attributeVersion);
    }

    public class IncidentReportAttributeService : IIncidentReportAttributeService
    {
        private readonly IIncidentReportAttributeRepository _incidentReportAttributeRepository;
        private readonly IIncidentReportAttributeItems _incidentReportAttributeItems;
        private readonly IIncidentReportAttributeValue _attributeValue;

        public IncidentReportAttributeService(IIncidentReportAttributeRepository incidentReportAttributeRepository,
                                                IIncidentReportAttributeItems incidentReportAttributeItems,
                                                IIncidentReportAttributeValue attributeValue)
        {
            _incidentReportAttributeRepository = incidentReportAttributeRepository;
            _incidentReportAttributeItems = incidentReportAttributeItems;
            _attributeValue = attributeValue;
        }

        public async Task CreateAttributeList(IEnumerable<AttributeViewModel> attributesVM, IncidentReport incidentReport)
        {
            var localAttributes = _incidentReportAttributeItems.GetAttributesHashSet(incidentReport.Kind, incidentReport.AttributesVersion);
            List<AttributeViewModel> attributesViewModel = new();

            foreach (var localAttribute in localAttributes)
            {
                var attribute = attributesVM.FirstOrDefault(x => x.Name == localAttribute.Name);

                AttributeRequiredCheck(attribute, localAttribute.IsRequired, localAttribute.Type.Name, localAttribute.Name);

                if (attribute != null) attributesViewModel.Add(_attributeValue.CreateAttribute(localAttribute, attribute));
            }

            var attributes = await _incidentReportAttributeRepository.GetListOfAttributesByIncidentReportIdAsync(incidentReport.Id).ConfigureAwait(false);
            await _incidentReportAttributeRepository.RemoveListOfAttributes(attributes).ConfigureAwait(false);
            await _incidentReportAttributeRepository.CreateRangeAttributes(CreateRangeIncidentReportAttributes(attributesViewModel, incidentReport.Id));
        }

        private static IEnumerable<IncidentReportAttribute> CreateRangeIncidentReportAttributes(IEnumerable<AttributeViewModel> createIncidentReportAttributes, Guid id)
        {
            return createIncidentReportAttributes.Select(x => new IncidentReportAttribute()
            {
                Id = $"{id}-{x.Name}",
                BoolValue = x.BoolValue,
                IncidentReportId = id,
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

        public HashSet<IncidentReportAttributeItem> GetAttributeList(IncidentReportKind kind, int attributeVersion)
        {
            return _incidentReportAttributeItems.GetAttributesHashSet(kind, attributeVersion);
        }
    }
}
