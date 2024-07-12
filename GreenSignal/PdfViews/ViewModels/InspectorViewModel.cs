using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace PdfViews.ViewModels
{
    public class InspectorViewModel
    {
        [Display(Name = "Ф.И.О.")]
        public string FIO { get; set; }

        [Display(Name = "Номер телефона")]
        public string Phone { get; set; }

        [Display(Name = "Номер инспектора")]
        public int Number { get; set; }

        [Display(Name = "Подпись")]
        public byte[] SavedFileBytes { get; set; }

        [Display(Name = "Номер служебного удостоверения")]
        public string Certificate { get; set; }

        [Display(Name = "Дата выдачи служебного удостоверения")]
        public string CertificateDate { get; set; }
    }
}
