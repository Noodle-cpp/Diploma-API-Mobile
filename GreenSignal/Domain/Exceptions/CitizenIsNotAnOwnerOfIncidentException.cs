using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class CitizenIsNotAnOwnerOfIncidentException : Exception
    {
        public CitizenIsNotAnOwnerOfIncidentException()
        {
        }

        public CitizenIsNotAnOwnerOfIncidentException(string? message) : base(message)
        {
        }

        public CitizenIsNotAnOwnerOfIncidentException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected CitizenIsNotAnOwnerOfIncidentException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
