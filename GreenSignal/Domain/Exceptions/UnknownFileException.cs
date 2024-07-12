using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class UnknownFileException : Exception
    {
        public UnknownFileException()
        {
        }

        public UnknownFileException(string? message) : base(message)
        {
        }

        public UnknownFileException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected UnknownFileException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
