using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class ScoreNotFoundException : Exception
    {
        public ScoreNotFoundException()
        {
        }

        public ScoreNotFoundException(string? message) : base(message)
        {
        }

        public ScoreNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected ScoreNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
