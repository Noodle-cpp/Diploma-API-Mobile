using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class PetitionAttachmentNotFoundException : Exception
    {
        public PetitionAttachmentNotFoundException()
        {
        }

        public PetitionAttachmentNotFoundException(string? message) : base(message)
        {
        }

        public PetitionAttachmentNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected PetitionAttachmentNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
