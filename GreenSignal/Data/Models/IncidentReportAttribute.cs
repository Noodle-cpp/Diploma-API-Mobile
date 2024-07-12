using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Data.Models
{
    public class IncidentReportAttribute
    {
        [Key]
        public string Id { get; set; }

        [Required]
        public Guid IncidentReportId { get; set; }

        public IncidentReport IncidentReport { get; set; }             

        [Required]
        public string Name { get; set; }

        public string? StringValue { get; set; }

        public double? NumberValue { get; set; }

        public bool? BoolValue { get; set; }
    }
}
