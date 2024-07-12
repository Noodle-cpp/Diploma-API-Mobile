using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class LocationNotFoundException : Exception
    {
        public LocationNotFoundException()
        {
        }

        public LocationNotFoundException(string? message) : base(message)
        {
        }

        public LocationNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected LocationNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
