using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class CodeIsTooOldException : Exception
    {
        public CodeIsTooOldException()
        {
        }

        public CodeIsTooOldException(string? message) : base(message)
        {
        }

        public CodeIsTooOldException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected CodeIsTooOldException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
