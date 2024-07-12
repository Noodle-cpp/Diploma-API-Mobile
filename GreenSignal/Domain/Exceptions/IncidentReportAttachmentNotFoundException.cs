using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class IncidentReportAttachmentNotFoundException : Exception
    {
        public IncidentReportAttachmentNotFoundException()
        {
        }

        public IncidentReportAttachmentNotFoundException(string? message) : base(message)
        {
        }

        public IncidentReportAttachmentNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected IncidentReportAttachmentNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
