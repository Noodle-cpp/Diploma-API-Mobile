using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class UploadAttachmentException : Exception
    {
        public UploadAttachmentException()
        {
        }

        public UploadAttachmentException(string? message) : base(message)
        {
        }

        public UploadAttachmentException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected UploadAttachmentException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
