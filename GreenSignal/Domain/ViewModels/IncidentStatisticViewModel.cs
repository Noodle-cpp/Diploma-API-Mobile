using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.ViewModels
{
    public class IncidentStatisticViewModel
    {
        public int CountOfIncidents { get; set; }
        public int CountOfCompletedIncidents { get; set; }
        public int CountOfIncidentsInWork { get; set; }
    }
}
