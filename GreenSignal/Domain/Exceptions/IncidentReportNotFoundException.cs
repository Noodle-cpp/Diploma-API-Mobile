using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class IncidentReportNotFoundException : Exception
    {
        public IncidentReportNotFoundException()
        {
        }

        public IncidentReportNotFoundException(string? message) : base(message)
        {
        }

        public IncidentReportNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected IncidentReportNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
