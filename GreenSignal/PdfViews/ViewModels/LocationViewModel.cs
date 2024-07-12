using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace PdfViews.ViewModels
{
    public class LocationViewModel
    {
        [Display(Name = "Название")]
        public string Name { get; set; }
    }
}
