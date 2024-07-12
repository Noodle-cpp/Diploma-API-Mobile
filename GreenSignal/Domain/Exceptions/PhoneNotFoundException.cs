using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class PhoneNotFoundException : Exception
    {
        public PhoneNotFoundException()
        {
        }

        public PhoneNotFoundException(string? message) : base(message)
        {
        }

        public PhoneNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected PhoneNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
