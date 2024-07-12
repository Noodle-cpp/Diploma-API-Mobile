using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class IncidentAttachmentNotFoundException : Exception
    {
        public IncidentAttachmentNotFoundException()
        {
        }

        public IncidentAttachmentNotFoundException(string? message) : base(message)
        {
        }

        public IncidentAttachmentNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected IncidentAttachmentNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
