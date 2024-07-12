using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace FileManager.Exceptions
{
    [Serializable]
    public class RemoveFileException : Exception
    {
        public RemoveFileException()
        {
        }

        public RemoveFileException(string? message) : base(message)
        {
        }

        public RemoveFileException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected RemoveFileException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
