using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class PetitionNotFoundException : Exception
    {
        public PetitionNotFoundException()
        {
        }

        public PetitionNotFoundException(string? message) : base(message)
        {
        }

        public PetitionNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected PetitionNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
