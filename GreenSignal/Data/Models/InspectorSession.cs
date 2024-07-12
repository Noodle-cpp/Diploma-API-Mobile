using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    public class InspectorSession
    {
        [Key]
        public Guid Id { get; set; }

        [Required]
        public string FirebaseToken { get; set; }

        [Required]
        public string DeviceName { get; set; }

        [Required]
        [MaxLength(16)]
        public string Ip { get; set; }

        public DateTime CretedAt { get; set; }

        [Required]
        public Guid InspectorId { get; set; }
        public Inspector Inspector { get; set; }
    }
}
