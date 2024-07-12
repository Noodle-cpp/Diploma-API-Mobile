using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PdfViews.ViewModels
{
    public class CitizenViewModel
    {
        [Display(Name = "Ф.И.О.")]
        public string FIO { get; set; }

        [Display(Name = "Номер телефона")]
        public string Phone { get; set; }
    }
}
