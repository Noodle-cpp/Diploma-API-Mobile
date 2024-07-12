using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class SavedFileNotFoundException : Exception
    {
        public SavedFileNotFoundException()
        {
        }

        public SavedFileNotFoundException(string? message) : base(message)
        {
        }

        public SavedFileNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected SavedFileNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
