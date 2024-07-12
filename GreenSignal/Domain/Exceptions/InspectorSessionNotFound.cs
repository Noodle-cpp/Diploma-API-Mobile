using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class InspectorSessionNotFound : Exception
    {
        public InspectorSessionNotFound()
        {
        }

        public InspectorSessionNotFound(string? message) : base(message)
        {
        }

        public InspectorSessionNotFound(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected InspectorSessionNotFound(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
