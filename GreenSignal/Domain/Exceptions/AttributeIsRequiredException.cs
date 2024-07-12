﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class AttributeIsRequiredException : Exception
    {
        public AttributeIsRequiredException()
        {
        }

        public AttributeIsRequiredException(string? message) : base(message)
        {
        }

        public AttributeIsRequiredException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected AttributeIsRequiredException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
