using Data.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.ViewModels
{
    public class InspectorRatingScore
    {
        public int Place { get; set; }
        public Guid InspectorId { get; set; }
        public Inspector Inspector { get; set; }
        public int TotalScore { get; set; }
    }
}
