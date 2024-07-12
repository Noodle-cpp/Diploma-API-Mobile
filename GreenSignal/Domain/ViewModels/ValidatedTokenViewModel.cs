using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.ViewModels
{
    public class ValidatedTokenViewModel
    {
        public Guid UserId { get; set; }
        public Guid? SessionId { get; set; }
    }
}
