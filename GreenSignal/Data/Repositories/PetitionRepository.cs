using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IPetitionRepository
    {
        Task<Petition?> GetPetitionByIdAsync(Guid id);
        Task CreatePetitionAsync(Petition petition);
        Task UpdatePetitionAsync(Petition petition);
        Task<IEnumerable<Petition>> GetPetitionListAsync(Guid inspectorId, int page, int perPage);
        Task<int> GetPetitionsCountAsync();
        Task<bool> IsPetitionAttached(Guid id);
    }

    public class PetitionRepository : IPetitionRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public PetitionRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task CreatePetitionAsync(Petition petition)
        {
            await _greenSignalContext.Petitions.AddAsync(petition).ConfigureAwait(false);
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }

        public async Task<Petition?> GetPetitionByIdAsync(Guid id)
        {
            return await _greenSignalContext.Petitions.AsNoTracking()
                                                        .Include(x => x.Attachments)
                                                            .ThenInclude(x => x.SavedFile)
                                                        .Include(x => x.Attributes)
                                                        .Include(x => x.ReceiveMessages)
                                                            .ThenInclude(x => x.MessageAttachments)
                                                                .ThenInclude(x => x.SavedFile)
                                                        .Include(x => x.Department)
                                                            .ThenInclude(x => x.Location)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.IncidentReport)
                                                                .ThenInclude(x => x.Inspector)
                                                                    .ThenInclude(x => x.PhotoFile)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.IncidentReport)
                                                                .ThenInclude(x => x.Inspector)
                                                                    .ThenInclude(x => x.CertificateFile)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.IncidentReport)
                                                                .ThenInclude(x => x.Incident)
                                                                    .ThenInclude(x => x.ReportedBy)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.IncidentReport)
                                                                .ThenInclude(x => x.IncidentReportAttributes)
                                                        .Include(x => x.ParentPetition)
                                                            .Include(x => x.IncidentReport)
                                                                .ThenInclude(x => x.IncidentReportAttachments)
                                                                    .ThenInclude(x => x.SavedFile)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.Inspector)
                                                                .ThenInclude(x => x.PhotoFile)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.Inspector)
                                                                .ThenInclude(x => x.CertificateFile)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.Department)
                                                                .ThenInclude(x => x.Location)
                                                        .Include(x => x.IncidentReport)
                                                            .ThenInclude(x => x.Inspector)
                                                        .Include(x => x.IncidentReport)
                                                            .ThenInclude(x => x.Incident)
                                                                .ThenInclude(x => x.ReportedBy)
                                                        .Include(x => x.IncidentReport)
                                                            .ThenInclude(x => x.IncidentReportAttributes)
                                                        .Include(x => x.IncidentReport)
                                                            .ThenInclude(x => x.IncidentReportAttachments)
                                                                .ThenInclude(x => x.SavedFile)
                                                        .Include(x => x.IncidentReport)
                                                        .Include(x => x.Inspector)
                                                            .ThenInclude(x => x.CertificateFile)
                                                        .Include(x => x.Inspector)
                                                            .ThenInclude(x => x.PhotoFile)
                                                        .Include(x => x.Inspector)
                                                            .ThenInclude(x => x.PhotoFile)
                                                        .FirstOrDefaultAsync(x => x.Id == id).ConfigureAwait(false);
        }

        public async Task<IEnumerable<Petition>> GetPetitionListAsync(Guid inspectorId, int page, int perPage)
        {
            return await _greenSignalContext.Petitions.AsNoTracking()
                                                        .Include(x => x.Attachments)
                                                            .ThenInclude(x => x.SavedFile)
                                                        .Include(x => x.Attributes)
                                                        .Include(x => x.ReceiveMessages)
                                                        .Include(x => x.Department)
                                                            .ThenInclude(x => x.Location)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.IncidentReport)
                                                                .ThenInclude(x => x.Inspector)
                                                                    .ThenInclude(x => x.PhotoFile)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.IncidentReport)
                                                                .ThenInclude(x => x.Inspector)
                                                                    .ThenInclude(x => x.CertificateFile)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.IncidentReport)
                                                                .ThenInclude(x => x.Incident)
                                                                    .ThenInclude(x => x.ReportedBy)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.IncidentReport)
                                                                .ThenInclude(x => x.IncidentReportAttributes)
                                                        .Include(x => x.ParentPetition)
                                                            .Include(x => x.IncidentReport)
                                                                .ThenInclude(x => x.IncidentReportAttachments)
                                                                    .ThenInclude(x => x.SavedFile)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.Inspector)
                                                                .ThenInclude(x => x.PhotoFile)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.Inspector)
                                                                .ThenInclude(x => x.CertificateFile)
                                                        .Include(x => x.ParentPetition)
                                                            .ThenInclude(x => x.Department)
                                                                .ThenInclude(x => x.Location)
                                                        .Include(x => x.IncidentReport)
                                                            .ThenInclude(x => x.Inspector)
                                                        .Include(x => x.IncidentReport)
                                                            .ThenInclude(x => x.Incident)
                                                                .ThenInclude(x => x.ReportedBy)
                                                        .Include(x => x.IncidentReport)
                                                            .ThenInclude(x => x.Location)
                                                        .Include(x => x.ParentPetition)
                                                        .Include(x => x.Inspector)
                                                            .ThenInclude(x => x.CertificateFile)
                                                        .Include(x => x.Inspector)
                                                            .ThenInclude(x => x.PhotoFile)
                                                        .Include(x => x.Inspector)
                                                            .ThenInclude(x => x.PhotoFile)
                                                        .Where(x => x.InspectorId == inspectorId && x.Status != PetitionStatus.Archived)
                                                        .Skip((page - 1) * perPage)
                                                        .Take(perPage)
                                                        .ToListAsync().ConfigureAwait(false);
        }

        public async Task<int> GetPetitionsCountAsync()
        {
            return await _greenSignalContext.Petitions.AsNoTracking().CountAsync().ConfigureAwait(false);
        }

        public async Task<bool> IsPetitionAttached(Guid id)
        {
            return await _greenSignalContext.Petitions.AnyAsync(x => x.ParentPetitionId == id && x.Status != PetitionStatus.Archived).ConfigureAwait(false);
        }

        public async Task UpdatePetitionAsync(Petition petition)
        {
            _greenSignalContext.Entry(petition).State = EntityState.Modified;
            await _greenSignalContext.SaveChangesAsync().ConfigureAwait(false);
        }
    }
}
