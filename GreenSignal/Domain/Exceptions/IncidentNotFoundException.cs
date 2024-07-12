using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class IncidentNotFoundException : Exception
    {
        public IncidentNotFoundException()
        {
        }

        public IncidentNotFoundException(string? message) : base(message)
        {
        }

        public IncidentNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected IncidentNotFoundException(System.Runtime.Serialization.SerializationInfo serializationInfo, System.Runtime.Serialization.StreamingContext streamingContext)
        {
            throw new NotImplementedException();
        }
    }
}
