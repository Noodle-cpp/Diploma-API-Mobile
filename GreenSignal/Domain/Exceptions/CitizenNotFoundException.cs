using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class CitizenNotFoundException : Exception
    {
        public CitizenNotFoundException()
        {
        }

        public CitizenNotFoundException(string? message) : base(message)
        {
        }

        public CitizenNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected CitizenNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
