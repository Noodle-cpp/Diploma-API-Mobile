using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class InspectorAlreadyReportIncidentException : Exception
    {
        public InspectorAlreadyReportIncidentException()
        {
        }

        public InspectorAlreadyReportIncidentException(string? message) : base(message)
        {
        }

        public InspectorAlreadyReportIncidentException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected InspectorAlreadyReportIncidentException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
