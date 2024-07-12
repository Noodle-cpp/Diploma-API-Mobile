using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    [PrimaryKey(nameof(IncidentId), nameof(InspectorId))]
    public class IncidentComplaint
    {
        public Guid IncidentId { get; set; }
        public Incident Incident { get; set; }

        public Guid InspectorId { get; set; }
        public Inspector Inspector { get; set; }
    }
}
