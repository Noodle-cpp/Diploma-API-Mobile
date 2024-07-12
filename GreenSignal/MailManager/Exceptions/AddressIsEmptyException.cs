using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace MailManager.Exceptions
{
    [Serializable]
    public class AddressIsEmptyException : Exception
    {
        public AddressIsEmptyException()
        {
        }

        public AddressIsEmptyException(string? message) : base(message)
        {
        }

        public AddressIsEmptyException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected AddressIsEmptyException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
