using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class CoordsAreEmptyException : Exception
    {
        public CoordsAreEmptyException()
        {
        }

        public CoordsAreEmptyException(string? message) : base(message)
        {
        }

        public CoordsAreEmptyException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected CoordsAreEmptyException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
