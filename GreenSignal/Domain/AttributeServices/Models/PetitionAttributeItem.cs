using Data.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.AttributeServices.Models
{
    public class PetitionAttributeItem : BaseAttributeItem
    {
        public PetitionKind Kind { get; set; }
    }
}
