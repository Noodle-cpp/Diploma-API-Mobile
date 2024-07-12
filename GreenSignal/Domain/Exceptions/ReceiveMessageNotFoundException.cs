using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class ReceiveMessageNotFoundException : Exception
    {
        public ReceiveMessageNotFoundException()
        {
        }

        public ReceiveMessageNotFoundException(string? message) : base(message)
        {
        }

        public ReceiveMessageNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected ReceiveMessageNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
