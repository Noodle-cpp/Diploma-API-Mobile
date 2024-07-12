using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.AttributeServices.Models
{
    public class BaseAttributeItem
    {
        public string Name { get; set; }
        public string Title { get; set; }
        public Type Type { get; set; }
        public int Version { get; set; }
        public bool IsRequired { get; set; }
    }
}
