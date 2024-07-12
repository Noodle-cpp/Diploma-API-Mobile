using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class CoordsAreTooOldException : Exception
    {
        public CoordsAreTooOldException()
        {
        }

        public CoordsAreTooOldException(string? message) : base(message)
        {
        }

        public CoordsAreTooOldException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected CoordsAreTooOldException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
