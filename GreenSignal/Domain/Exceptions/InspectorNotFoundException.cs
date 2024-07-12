using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class InspectorNotFoundException : Exception
    {
        public InspectorNotFoundException()
        {
        }

        public InspectorNotFoundException(string? message) : base(message)
        {
        }

        public InspectorNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected InspectorNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
