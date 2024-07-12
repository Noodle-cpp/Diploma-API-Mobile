using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class CodeNotFoundException : Exception
    {
        public CodeNotFoundException()
        {
        }

        public CodeNotFoundException(string? message) : base(message)
        {
        }

        public CodeNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected CodeNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
