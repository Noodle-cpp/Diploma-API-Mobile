using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class ParentPetitionNotFoundException : Exception
    {
        public ParentPetitionNotFoundException()
        {
        }

        public ParentPetitionNotFoundException(string? message) : base(message)
        {
        }

        public ParentPetitionNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected ParentPetitionNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
