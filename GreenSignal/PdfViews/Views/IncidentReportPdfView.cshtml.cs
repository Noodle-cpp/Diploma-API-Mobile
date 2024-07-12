using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using PdfViews.ViewModels;
using System.Globalization;
using System.Text.RegularExpressions;

namespace PdfViews.Views
{
    public class IncidentReportPdfViewModel : PageModel
    {
        public IncidentReportViewModel IncidentReport { get; set; }
    }
}
