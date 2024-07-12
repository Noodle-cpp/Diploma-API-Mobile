using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using PdfViews.ViewModels;

namespace PdfViews.Views
{
    public class PetitionPdfViewModel : PageModel
    {
        public PetitionViewModel Petition { get; set; }
        public IncidentReportViewModel IncidentReport { get; set; }
    }
}
