using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PdfViews.ViewModels
{
    public class IncidentReportAttributeViewModel
    {
        [Display(Name = "Имя")]
        public string Name { get; set; }

        [Display(Name = "Название")]
        public string Title { get; set; }

        [Display(Name = "Значение")]
        public string? StringValue { get; set; }

        [Display(Name = "Значение")]
        public double? NumberValue { get; set; }

        [Display(Name = "Значение")]
        public bool? BoolValue { get; set; }
    }
}
