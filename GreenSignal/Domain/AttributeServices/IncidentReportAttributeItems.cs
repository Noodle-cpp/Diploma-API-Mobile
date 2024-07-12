using Data.Models;
using Domain.AttributeServices.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.AttributeServices
{
    public interface IIncidentReportAttributeItems
    {
        public HashSet<IncidentReportAttributeItem> GetAttributesHashSet(IncidentReportKind kind, int version);
    }

    public class IncidentReportAttributeItems : IIncidentReportAttributeItems
    {
        readonly HashSet<IncidentReportAttributeItem> incidentReportAttributeList = new();

        private readonly string fuelType = "FUEL_TYPE";
        private readonly string fuelTypeTitle = "Вид используемого топлива";

        private readonly string trashContent = "TRASH_CONTENT";
        private readonly string trashContentTitle = "Вид отходов";

        private readonly string barrier = "BARRIER";
        private readonly string barrierTitle = "Наличие ограждений, шлагбаумов";

        private readonly IEnumerable<string> squareCoords = new List<string>() { "GEO_1_LAT", "GEO_1_LNG", "GEO_2_LAT", "GEO_2_LNG", "GEO_3_LAT", "GEO_3_LNG", "GEO_4_LAT", "GEO_4_LNG" };
        private readonly string squareCoordsTitle = "Координаты площади";

        private readonly string events = "EVENTS";
        private readonly string eventsTitle = "Перечень мероприятий";

        private readonly string cadastralNember = "CADASTRAL_NUMBER";
        private readonly string cadastralNemberTitle = "Кадастровый номер";

        private readonly string sourceDescription = "SOURCE_DESCRIPTION";
        private readonly string sourceDescriptionTitle = "Описание источника";

        private readonly string territoryDescription = "TERRITORY_DESCRIPTION";
        private readonly string territoryDescriptionTitle = "Описание территории";

        private readonly string driveways = "DRIVEWAYS";
        private readonly string drivewaysTitle = "Наличие подъездных путей";

        private readonly string square = "SQUARE";
        private readonly string squareTitle = "Площадь нарушения";

        private readonly string volume = "VOLUME";
        private readonly string volumeTitle = "Примерный объем";

        private readonly string typeOfMineral = "TYPE_OF_MINIRAl";
        private readonly string typeOfMineralTitle = "Вид полезного ископаемого";

        private readonly string woodType = "WOOD_TYPE";
        private readonly string woodTypeTitle = "Порода древесины";

        private readonly string countOfStumps = "COUNT_OF_STUMPS";
        private readonly string countOfStumpsTitle = "Количество пеньков";

        private readonly string diameter = "DIAMETER";
        private readonly string diameterTitle = "Диаметр";


        public IncidentReportAttributeItems()
        {
            #region AirPollution

            #region version_1

            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(barrier, barrierTitle, true, IncidentReportKind.AirPollution, typeof(bool), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(driveways, drivewaysTitle, true, IncidentReportKind.AirPollution, typeof(bool), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(territoryDescription, territoryDescriptionTitle, true, IncidentReportKind.AirPollution, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(sourceDescription, sourceDescriptionTitle, true, IncidentReportKind.AirPollution, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(cadastralNember, cadastralNemberTitle, false, IncidentReportKind.AirPollution, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(events, eventsTitle, false, IncidentReportKind.AirPollution, typeof(string), 1));

            #endregion

            #endregion

            #region Dump

            #region version_1

            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(volume, volumeTitle, true, IncidentReportKind.Dump, typeof(double), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(square, squareTitle, true, IncidentReportKind.Dump, typeof(double), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(driveways, drivewaysTitle, true, IncidentReportKind.Dump, typeof(bool), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(barrier, barrierTitle, true, IncidentReportKind.Dump, typeof(bool), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(territoryDescription, territoryDescriptionTitle, true, IncidentReportKind.Dump, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(sourceDescription, sourceDescriptionTitle, false, IncidentReportKind.Dump, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(trashContent, trashContentTitle, true, IncidentReportKind.Dump, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(events, eventsTitle, false, IncidentReportKind.Dump, typeof(string), 1));

            foreach (var dumpCoord in squareCoords)
                incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(dumpCoord, squareCoordsTitle, true, IncidentReportKind.Dump, typeof(double), 1));

            #endregion

            #endregion

            #region SoilPollution

            #region version_1

            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(driveways, drivewaysTitle, true, IncidentReportKind.SoilPollution, typeof(bool), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(trashContent, trashContentTitle, true, IncidentReportKind.SoilPollution, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(fuelType, fuelTypeTitle, true, IncidentReportKind.SoilPollution, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(sourceDescription, sourceDescriptionTitle, false, IncidentReportKind.SoilPollution, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(territoryDescription, territoryDescriptionTitle, true, IncidentReportKind.SoilPollution, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(events, eventsTitle, false, IncidentReportKind.SoilPollution, typeof(string), 1));

            #endregion

            #endregion

            #region Excavation

            #region version_1

            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(typeOfMineral, typeOfMineralTitle, true, IncidentReportKind.Excavation, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(volume, volumeTitle, true, IncidentReportKind.Excavation, typeof(double), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(square, squareTitle, true, IncidentReportKind.Excavation, typeof(double), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(driveways, drivewaysTitle, true, IncidentReportKind.Excavation, typeof(bool), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(barrier, barrierTitle, true, IncidentReportKind.Excavation, typeof(bool), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(territoryDescription, territoryDescriptionTitle, true, IncidentReportKind.Excavation, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(events, eventsTitle, false, IncidentReportKind.Excavation, typeof(string), 1));

            foreach (var excavationCoord in squareCoords)
                incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(excavationCoord, squareCoordsTitle, true, IncidentReportKind.Excavation, typeof(double), 1));

            #endregion

            #endregion

            #region TreeCutting

            #region version_1

            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(diameter, diameterTitle, false, IncidentReportKind.TreeCutting, typeof(double), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(countOfStumps, countOfStumpsTitle, false, IncidentReportKind.TreeCutting, typeof(double), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(woodType, woodTypeTitle, true, IncidentReportKind.TreeCutting, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(volume, volumeTitle, true, IncidentReportKind.TreeCutting, typeof(double), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(square, squareTitle, true, IncidentReportKind.TreeCutting, typeof(double), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(driveways, drivewaysTitle, true, IncidentReportKind.TreeCutting, typeof(bool), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(territoryDescription, territoryDescriptionTitle, true, IncidentReportKind.TreeCutting, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(events, eventsTitle, false, IncidentReportKind.TreeCutting, typeof(string), 1));

            foreach (var treeCuttingCoord in squareCoords)
                incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(treeCuttingCoord, squareCoordsTitle, true, IncidentReportKind.TreeCutting, typeof(double), 1));

            #endregion

            #endregion

            #region Radiation

            #region version_1

            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(volume, volumeTitle, true, IncidentReportKind.Radiation, typeof(double), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(square, squareTitle, true, IncidentReportKind.Radiation, typeof(double), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(driveways, drivewaysTitle, true, IncidentReportKind.Radiation, typeof(bool), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(barrier, barrierTitle, true, IncidentReportKind.Radiation, typeof(bool), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(territoryDescription, territoryDescriptionTitle, true, IncidentReportKind.Radiation, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(sourceDescription, sourceDescriptionTitle, false, IncidentReportKind.Radiation, typeof(string), 1));
            incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(events, eventsTitle, false, IncidentReportKind.Radiation, typeof(string), 1));

            foreach (var radiationCoord in squareCoords)
                incidentReportAttributeList.Add(CreateIncidentReportAttributeItem(radiationCoord, squareCoordsTitle, true, IncidentReportKind.Radiation, typeof(double), 1));

            #endregion

            #endregion
        }

        public HashSet<IncidentReportAttributeItem> GetAttributesHashSet(IncidentReportKind kind, int version)
        {
            return incidentReportAttributeList.Where(x => x.Kind == kind && x.Version == version).ToHashSet();
        }

        private static IncidentReportAttributeItem CreateIncidentReportAttributeItem(string name, string title, bool isRequired, IncidentReportKind kind, Type type, int version)
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
