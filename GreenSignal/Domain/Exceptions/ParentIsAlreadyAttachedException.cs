using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class ParentIsAlreadyAttachedException : Exception
    {
        public ParentIsAlreadyAttachedException()
        {
        }

        public ParentIsAlreadyAttachedException(string? message) : base(message)
        {
        }

        public ParentIsAlreadyAttachedException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected ParentIsAlreadyAttachedException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
