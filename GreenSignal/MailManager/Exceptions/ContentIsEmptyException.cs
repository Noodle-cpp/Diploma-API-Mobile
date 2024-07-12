using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace MailManager.Exceptions
{
    [Serializable]
    public class ContentIsEmptyException : Exception
    {
        public ContentIsEmptyException()
        {
        }

        public ContentIsEmptyException(string? message) : base(message)
        {
        }

        public ContentIsEmptyException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected ContentIsEmptyException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
