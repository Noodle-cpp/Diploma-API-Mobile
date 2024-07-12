using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class PetitionCantBeAParentOfHimselfException : Exception
    {
        public PetitionCantBeAParentOfHimselfException()
        {
        }

        public PetitionCantBeAParentOfHimselfException(string? message) : base(message)
        {
        }

        public PetitionCantBeAParentOfHimselfException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected PetitionCantBeAParentOfHimselfException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
