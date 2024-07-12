using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace FileManager.Exceptions
{
    [Serializable]
    public class DownloadFileException : Exception
    {
        public DownloadFileException()
        {
        }

        public DownloadFileException(string? message) : base(message)
        {
        }

        public DownloadFileException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected DownloadFileException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
