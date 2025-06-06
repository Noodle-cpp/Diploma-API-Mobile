﻿using Data.Models;
using System.ComponentModel.DataAnnotations;

namespace Api.ViewModels.Requests
{
    public class CreateIncidentViewModel
    {
        [MaxLength(10000)]
        public string Description { get; set; }

        [MaxLength(10000)]
        public string Address { get; set; }

        public double? Lat { get; set; }

        public double? Lng { get; set; }
        
        [Required]
        public IncidentKind Kind { get; set; }
    }
}
