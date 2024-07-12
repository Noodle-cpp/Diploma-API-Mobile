using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PdfViews.ViewModels
{
    public class IncidentReportAttachmentViewModel
    {
        [Display(Name = "Файл")]
        public byte[] SavedFileBytes { get; set; }

        [Display(Name = "Описание")]
        public string Description { get; set; }

        [Display(Name = "Дата фиксации")]
        public DateTime ManualDate { get; set; }
    }
}
