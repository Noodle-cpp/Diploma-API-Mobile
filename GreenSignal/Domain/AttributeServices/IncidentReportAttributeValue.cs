using Domain.AttributeServices.Models;
using Domain.ViewModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.AttributeServices
{
    public interface IIncidentReportAttributeValue
    {
        AttributeViewModel CreateAttribute(IncidentReportAttributeItem attribute,
                                            AttributeViewModel createAttributeVM);
    }

    public class IncidentReportAttributeValue : IIncidentReportAttributeValue
    {
        #region IncidentReport

        /// <summary>
        /// Создать атрибут для акта
        /// </summary>
        /// <param name="attribute"></param>
        /// <param name="createAttributeVM"></param>
        /// <returns></returns>
        public AttributeViewModel CreateAttribute(IncidentReportAttributeItem attribute,
                                                 AttributeViewModel createAttributeVM)
        {
            return DirectAttributeByType(attribute.Type, createAttributeVM);
        }

        #endregion

        /// <summary>
        /// Создать атрибут соответствующий его типу
        /// </summary>
        /// <param name="type"></param>
        /// <param name="createAttributeVM"></param>
        /// <returns></returns>
        private static AttributeViewModel DirectAttributeByType(Type type, AttributeViewModel createAttributeVM)
        {
            switch (Type.GetTypeCode(type))
            {
                case (TypeCode.String):
                    {
                        return CreateStringAttribute(createAttributeVM);
                    }
                case (TypeCode.Boolean):
                    {
                        return CreateBooleanAttribute(createAttributeVM);
                    }
                case (TypeCode.Double):
                    {
                        return CreateNumberAttribute(createAttributeVM);
                    }
                default:
                    {
                        throw new TypeInitializationException(nameof(type), new("This type could not be find"));
                    }
            }
        }

        /// <summary>
        /// Создать строковый атрибут
        /// </summary>
        /// <param name="createAttributeVM"></param>
        /// <returns></returns>
        private static AttributeViewModel CreateStringAttribute(AttributeViewModel createAttributeVM)
        {
            if (createAttributeVM.StringValue != null)
            {
                AttributeViewModel attributeVM = new()
                {
                    Name = createAttributeVM.Name,
                    StringValue = createAttributeVM.StringValue
                };
                return attributeVM;
            }
            else throw new ArgumentException($"Attribte type: string, attribute Name: {createAttributeVM.Name}", nameof(createAttributeVM));
        }

        /// <summary>
        /// Создать логический атрибут
        /// </summary>
        /// <param name="createAttributeVM"></param>
        /// <returns></returns>
        private static AttributeViewModel CreateBooleanAttribute(AttributeViewModel createAttributeVM)
        {
            if (createAttributeVM.BoolValue != null)
            {
                AttributeViewModel attributeVM = new()
                {
                    Name = createAttributeVM.Name,
                    BoolValue = createAttributeVM.BoolValue
                };

                return attributeVM;
            }
            else throw new ArgumentException($"Attribte type: boolean, attribute Name: {createAttributeVM.Name}", nameof(createAttributeVM));
        }

        /// <summary>
        /// Создать числовой атрибут
        /// </summary>
        /// <param name="createAttributeVM"></param>
        /// <returns></returns>
        private static AttributeViewModel CreateNumberAttribute(AttributeViewModel createAttributeVM)
        {
            if (createAttributeVM.NumberValue != null)
            {
                AttributeViewModel attributeVM = new()
                {
                    Name = createAttributeVM.Name,
                    NumberValue = createAttributeVM.NumberValue
                };

                return attributeVM;
            }
            else throw new ArgumentException($"Attribte type: double, attribute Name: {createAttributeVM.Name}", nameof(createAttributeVM));
        }
    }
}
