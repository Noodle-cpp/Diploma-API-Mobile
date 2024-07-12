using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class InspectorAlreadyExistsException : Exception
    {
        public InspectorAlreadyExistsException()
        {
        }

        public InspectorAlreadyExistsException(string? message) : base(message)
        {
        }

        public InspectorAlreadyExistsException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected InspectorAlreadyExistsException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
