using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class PetitionIsAlreadyCloseException : Exception
    {
        public PetitionIsAlreadyCloseException()
        {
        }

        public PetitionIsAlreadyCloseException(string? message) : base(message)
        {
        }

        public PetitionIsAlreadyCloseException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected PetitionIsAlreadyCloseException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
