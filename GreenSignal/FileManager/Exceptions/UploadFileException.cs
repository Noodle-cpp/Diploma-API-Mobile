using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace FileManager.Exceptions
{
    [Serializable]
    public class UploadFileException : Exception
    {
        public UploadFileException()
        {
        }

        public UploadFileException(string? message) : base(message)
        {
        }

        public UploadFileException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected UploadFileException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
