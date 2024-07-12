using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.ViewModels
{
    public class AuthenticationToken
    {
        public string Token { get; set; }

        public Guid? InspectorId { get; set; }

        public Guid? CitizenId { get; set; }
        public Guid? SessionId { get; set;}
    }
}
