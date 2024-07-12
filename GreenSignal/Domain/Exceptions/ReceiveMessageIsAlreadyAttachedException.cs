using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class ReceiveMessageIsAlreadyAttachedException : Exception
    {
        public ReceiveMessageIsAlreadyAttachedException()
        {
        }

        public ReceiveMessageIsAlreadyAttachedException(string? message) : base(message)
        {
        }

        public ReceiveMessageIsAlreadyAttachedException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected ReceiveMessageIsAlreadyAttachedException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
