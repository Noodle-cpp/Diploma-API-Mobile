using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class PetitionAlreadyHasAParentException : Exception
    {
        public PetitionAlreadyHasAParentException()
        {
        }

        public PetitionAlreadyHasAParentException(string? message) : base(message)
        {
        }

        public PetitionAlreadyHasAParentException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected PetitionAlreadyHasAParentException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
