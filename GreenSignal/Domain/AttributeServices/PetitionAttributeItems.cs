using Data.Models;
using Domain.AttributeServices.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.AttributeServices
{
    public interface IPetitionAttributeItems
    {
        public HashSet<PetitionAttributeItem> GetAttributesHashSet(PetitionKind kind, int version);
    }

    public class PetitionAttributeItems : IPetitionAttributeItems
    {
        readonly HashSet<PetitionAttributeItem> petitionAttributeList = new();

        private readonly string fuelType = "FUEL_TYPE";
        private readonly string fuelTypeTitle = "Вид используемого топлива";

        private readonly string trashContent = "TRASH_CONTENT";
        private readonly string trashContentTitle = "Вид отходов";

        private readonly string barrier = "BARRIER";
        private readonly string barrierTitle = "Наличие ограждений, шлагбаумов";

        private readonly IEnumerable<string> squareCoords = new List<string>() { "GEO_1_LAT", "GEO_1_LNG", "GEO_2_LAT", "GEO_2_LNG", "GEO_3_LAT", "GEO_3_LNG", "GEO_4_LAT", "GEO_4_LNG" };
        private readonly string squareCoordsTitle = "Координаты";

        private readonly string events = "EVENTS";
        private readonly string eventsTitle = "Перечень мероприятий";

        private readonly string cadastralNember = "CADASTRAL_NUMBER";
        private readonly string cadastralNemberTitle = "Кадастровый номер";

        private readonly string sourceDescription = "SOURCE_DESCRIPTION";
        private readonly string sourceDescriptionTitle = "Описание источника";

        private readonly string territoryDescription = "TERRITORY_DESCRIPTION";
        private readonly string territoryDescriptionTitle = "Описание территории";

        private readonly string isOffsiteEvents = "OFFSITE_EVENTS";
        private readonly string isOffsiteEventsTitle = "Прошу привлечь к выездным мероприятиям";

        private readonly string isMaterials = "MATERIALS";
        private readonly string isMaterialsTitle = "Прошу предоставить возможность ознакомиться с материалами";

        private readonly string isBringToJustice = "BRING_TO_JUSTICE";
        private readonly string isBringToJusticeTitle = "Прошу установить и привлечь виновных к ответственности";

        private readonly string isExamination = "EXAMINATION";
        private readonly string isExaminationTitle = "Прошу провести проверку в соответствии с ФЗ";

        private readonly string description = "DESCRIPTION";
        private readonly string descriptionTitle = "Пояснение к акту";

        private readonly string requirements = "REQUIREMENTS";
        private readonly string requirementsTitle = "Требования к надзорному органу";

        private readonly string driveways = "DRIVEWAYS";
        private readonly string drivewaysTitle = "Наличие подъездных путей";

        private readonly string square = "SQUARE";
        private readonly string squareTitle = "Площадь нарушения";

        private readonly string volume = "VOLUME";
        private readonly string volumeTitle = "Примерный объем ";

        public PetitionAttributeItems()
        {
            #region AirPollution

            #region version_1

            petitionAttributeList.Add(CreatePetitionAttributeItem(driveways, drivewaysTitle, true, PetitionKind.AirPollution, typeof(bool), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(territoryDescription, territoryDescriptionTitle, true, PetitionKind.AirPollution, typeof(string), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(sourceDescription, sourceDescriptionTitle, true, PetitionKind.AirPollution, typeof(string), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(cadastralNember, cadastralNemberTitle, false, PetitionKind.AirPollution, typeof(string), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(events, eventsTitle, true, PetitionKind.AirPollution, typeof(string), 1));

            #endregion

            #region version_2

            petitionAttributeList.Add(CreatePetitionAttributeItem(isOffsiteEvents, isOffsiteEventsTitle, true, PetitionKind.AirPollution, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isMaterials, isMaterialsTitle, true, PetitionKind.AirPollution, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isBringToJustice, isBringToJusticeTitle, true, PetitionKind.AirPollution, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isExamination, isExaminationTitle, true, PetitionKind.AirPollution, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(description, descriptionTitle, true, PetitionKind.AirPollution, typeof(string), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(requirements, requirementsTitle, true, PetitionKind.AirPollution, typeof(string), 2));

            #endregion

            #endregion

            #region Dump

            #region version_1

            petitionAttributeList.Add(CreatePetitionAttributeItem(volume, volumeTitle, true, PetitionKind.Dump, typeof(bool), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(square, squareTitle, true, PetitionKind.Dump, typeof(double), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(barrier, barrierTitle, true, PetitionKind.Dump, typeof(bool), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(territoryDescription, territoryDescriptionTitle, true, PetitionKind.Dump, typeof(string), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(sourceDescription, sourceDescriptionTitle, false, PetitionKind.Dump, typeof(string), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(trashContent, trashContentTitle, true, PetitionKind.Dump, typeof(string), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(driveways, drivewaysTitle, true, PetitionKind.Dump, typeof(bool), 1));

            foreach (var dumpCoord in squareCoords)
                petitionAttributeList.Add(CreatePetitionAttributeItem(dumpCoord, squareCoordsTitle, true, PetitionKind.Dump, typeof(double), 1));

            #endregion

            #region version_2

            petitionAttributeList.Add(CreatePetitionAttributeItem(isOffsiteEvents, isOffsiteEventsTitle, true, PetitionKind.Dump, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isMaterials, isMaterialsTitle, true, PetitionKind.Dump, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isBringToJustice, isBringToJusticeTitle, true, PetitionKind.Dump, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isExamination, isExaminationTitle, true, PetitionKind.Dump, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(description, descriptionTitle, true, PetitionKind.Dump, typeof(string), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(requirements, requirementsTitle, true, PetitionKind.Dump, typeof(string), 2));

            #endregion

            #endregion

            #region SoilPollution

            #region version_1

            petitionAttributeList.Add(CreatePetitionAttributeItem(driveways, drivewaysTitle, true, PetitionKind.SoilPollution, typeof(bool), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(trashContent, trashContentTitle, true, PetitionKind.SoilPollution, typeof(string), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(fuelType, fuelTypeTitle, true, PetitionKind.SoilPollution, typeof(string), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(sourceDescription, sourceDescriptionTitle, false, PetitionKind.SoilPollution, typeof(string), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(territoryDescription, territoryDescriptionTitle, true, PetitionKind.SoilPollution, typeof(string), 1));

            #endregion

            #region version_2

            petitionAttributeList.Add(CreatePetitionAttributeItem(isOffsiteEvents, isOffsiteEventsTitle, true, PetitionKind.SoilPollution, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isMaterials, isMaterialsTitle, true, PetitionKind.SoilPollution, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isBringToJustice, isBringToJusticeTitle, true, PetitionKind.SoilPollution, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isExamination, isExaminationTitle, true, PetitionKind.SoilPollution, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(description, descriptionTitle, true, PetitionKind.SoilPollution, typeof(string), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(requirements, requirementsTitle, true, PetitionKind.SoilPollution, typeof(string), 2));

            #endregion

            #endregion

            #region Excavation

            #region version_1

            petitionAttributeList.Add(CreatePetitionAttributeItem(volume, volumeTitle, true, PetitionKind.Excavation, typeof(double), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(square, squareTitle, true, PetitionKind.Excavation, typeof(double), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(driveways, drivewaysTitle, true, PetitionKind.Excavation, typeof(bool), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(barrier, barrierTitle, true, PetitionKind.Excavation, typeof(bool), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(territoryDescription, territoryDescriptionTitle, true, PetitionKind.Excavation, typeof(string), 1));
            foreach (var excavationCoord in squareCoords)
                petitionAttributeList.Add(CreatePetitionAttributeItem(excavationCoord, squareCoordsTitle, true, PetitionKind.Excavation, typeof(double), 1));

            #endregion

            #region version_2

            petitionAttributeList.Add(CreatePetitionAttributeItem(isOffsiteEvents, isOffsiteEventsTitle, true, PetitionKind.Excavation, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isMaterials, isMaterialsTitle, true, PetitionKind.Excavation, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isBringToJustice, isBringToJusticeTitle, true, PetitionKind.Excavation, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isExamination, isExaminationTitle, true, PetitionKind.Excavation, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(description, descriptionTitle, true, PetitionKind.Excavation, typeof(string), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(requirements, requirementsTitle, true, PetitionKind.Excavation, typeof(string), 2));

            #endregion

            #endregion

            #region TreeCutting

            #region version_1

            petitionAttributeList.Add(CreatePetitionAttributeItem(square, squareTitle, true, PetitionKind.TreeCutting, typeof(double), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(driveways, drivewaysTitle, true, PetitionKind.TreeCutting, typeof(bool), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(territoryDescription, territoryDescriptionTitle, true, PetitionKind.TreeCutting, typeof(string), 1));
            foreach (var treeCuttingCoord in squareCoords)
                petitionAttributeList.Add(CreatePetitionAttributeItem(treeCuttingCoord, squareCoordsTitle, true, PetitionKind.TreeCutting, typeof(double), 1));

            #endregion

            #region version_2

            petitionAttributeList.Add(CreatePetitionAttributeItem(isOffsiteEvents, isOffsiteEventsTitle, true, PetitionKind.TreeCutting, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isMaterials, isMaterialsTitle, true, PetitionKind.TreeCutting, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isBringToJustice, isBringToJusticeTitle, true, PetitionKind.TreeCutting, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isExamination, isExaminationTitle, true, PetitionKind.TreeCutting, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(description, descriptionTitle, true, PetitionKind.TreeCutting, typeof(string), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(requirements, requirementsTitle, true, PetitionKind.TreeCutting, typeof(string), 2));

            #endregion

            #endregion

            #region Radiation

            #region version_1

            petitionAttributeList.Add(CreatePetitionAttributeItem(volume, volumeTitle, true, PetitionKind.Radiation, typeof(double), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(square, squareTitle, true, PetitionKind.Radiation, typeof(double), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(driveways, drivewaysTitle, true, PetitionKind.Radiation, typeof(bool), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(barrier, barrierTitle, true, PetitionKind.Radiation, typeof(bool), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(territoryDescription, territoryDescriptionTitle, true, PetitionKind.Radiation, typeof(string), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(sourceDescription, sourceDescriptionTitle, false, PetitionKind.Radiation, typeof(string), 1));
            petitionAttributeList.Add(CreatePetitionAttributeItem(events, eventsTitle, true, PetitionKind.Radiation, typeof(string), 1));

            foreach (var radiationCoord in squareCoords)
                petitionAttributeList.Add(CreatePetitionAttributeItem(radiationCoord, squareCoordsTitle, true, PetitionKind.Radiation, typeof(double), 1));

            #endregion

            #region version_2

            petitionAttributeList.Add(CreatePetitionAttributeItem(isOffsiteEvents, isOffsiteEventsTitle, true, PetitionKind.Radiation, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isMaterials, isMaterialsTitle, true, PetitionKind.Radiation, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isBringToJustice, isBringToJusticeTitle, true, PetitionKind.Radiation, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(isExamination, isExaminationTitle, true, PetitionKind.Radiation, typeof(bool), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(description, descriptionTitle, true, PetitionKind.Radiation, typeof(string), 2));
            petitionAttributeList.Add(CreatePetitionAttributeItem(requirements, requirementsTitle, true, PetitionKind.Radiation, typeof(string), 2));

            #endregion

            #endregion
        }

        public HashSet<PetitionAttributeItem> GetAttributesHashSet(PetitionKind kind, int version)
        {
            return petitionAttributeList.Where(x => x.Kind == kind && x.Version == version).ToHashSet();
        }

        private static PetitionAttributeItem CreatePetitionAttributeItem(string name, string title, bool isRequired, PetitionKind kind, Type type, int version)
        {
            return new()
            {
                Name = name,
                Title = title,
                IsRequired = isRequired,
                Kind = kind,
                Type = type,
                Version = version
            };
        }
    }
}
