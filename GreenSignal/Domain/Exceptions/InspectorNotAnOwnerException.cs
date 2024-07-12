using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class InspectorNotAnOwnerException : Exception
    {
        public InspectorNotAnOwnerException()
        {
        }

        public InspectorNotAnOwnerException(string? message) : base(message)
        {
        }

        public InspectorNotAnOwnerException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected InspectorNotAnOwnerException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
