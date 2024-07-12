using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    public class PetitionAttribute
    {
        [Key]
        public string Id { get; set; }

        [Required]
        public Guid PetitionId { get; set; }

        public Petition Petition { get; set; }

        [Required]
        public string Name { get; set; }

        public string? StringValue { get; set; }

        public double? NumberValue { get; set; }

        public bool? BoolValue { get; set; }
    }
}
