using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class PetitionWasNotSentToADepartmentException : Exception
    {
        public PetitionWasNotSentToADepartmentException()
        {
        }

        public PetitionWasNotSentToADepartmentException(string? message) : base(message)
        {
        }

        public PetitionWasNotSentToADepartmentException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected PetitionWasNotSentToADepartmentException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
