using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class PetitionMustHaveAParentException : Exception
    {
        public PetitionMustHaveAParentException()
        {
        }

        public PetitionMustHaveAParentException(string? message) : base(message)
        {
        }

        public PetitionMustHaveAParentException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected PetitionMustHaveAParentException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
