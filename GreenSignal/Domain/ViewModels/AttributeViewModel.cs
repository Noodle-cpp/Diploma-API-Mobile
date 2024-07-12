using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.ViewModels
{
    public class AttributeViewModel
    {
        public string Name { get; set; }
        public string StringValue { get; set; }
        public double? NumberValue { get; set; }
        public bool? BoolValue { get; set; }
    }
}
