using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Exceptions
{
    [Serializable]
    public class DepartmentNotFoundException : Exception
    {
        public DepartmentNotFoundException()
        {
        }

        public DepartmentNotFoundException(string? message) : base(message)
        {
        }

        public DepartmentNotFoundException(string? message, Exception? innerException) : base(message, innerException)
        {
        }

        protected DepartmentNotFoundException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}
