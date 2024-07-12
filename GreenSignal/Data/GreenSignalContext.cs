using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    public class GreenSignalContext : DbContext
    {
        public GreenSignalContext(DbContextOptions options) : base(options)
        {
        }

        protected GreenSignalContext()
        {
        }

        protected override void OnModelCreating(ModelBuilder builder)
        {
        }

        public DbSet<Inspector> Inspectors { get; set; }
        public DbSet<Citizen> Citizens { get; set; }
        public DbSet<Code> Codes { get; set; }
        public DbSet<Incident> Incidents { get; set; }
        public DbSet<IncidentReport> IncidentReports { get; set; }
        public DbSet<IncidentReportAttachment> IncidentReportAttachments { get; set; }
        public DbSet<IncidentReportAttribute> IncidentReportAttributes { get; set; }
        public DbSet<Location> Locations { get; set; }
        public DbSet<SavedFile> SavedFiles { get; set; }
        public DbSet<InspectorSession> InspectorSessions { get; set; }
        public DbSet<ReceiveMessage> ReceiveMessages { get; set; }
        public DbSet<IncidentAttachment> IncidentAttachments { get; set; }
        public DbSet<Department> Departments { get; set; }
        public DbSet<Petition> Petitions { get; set; }
        public DbSet<PetitionAttachment> PetitionAttachments { get; set; }
        public DbSet<PetitionAttribute> PetitionAttributes { get; set; }
        public DbSet<MessageAttachment> MessageAttachments { get; set; }
        public DbSet<InspectorScore> InspectorScores { get; set; }
        public DbSet<IncidentComplaint> IncidentComplaints { get; set; }
    }
}
