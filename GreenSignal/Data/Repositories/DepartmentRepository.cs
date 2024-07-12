using Data.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Repositories
{
    public interface IDepartmentRepository
    {
        Task<Department?> GetDepartmentByIdAsync(Guid id);
        Task<IEnumerable<Department>> GetDepartmentListAsync(int page, int perPage);
    }

    public class DepartmentRepository : IDepartmentRepository
    {
        private readonly GreenSignalContext _greenSignalContext;

        public DepartmentRepository(GreenSignalContext greenSignalContext)
        {
            _greenSignalContext = greenSignalContext;
        }

        public async Task<Department?> GetDepartmentByIdAsync(Guid id)
        {
            return await _greenSignalContext.Departments.Include(x => x.Location)
                                                        .FirstOrDefaultAsync(x => x.Id == id).ConfigureAwait(false);
        }

        public async Task<IEnumerable<Department>> GetDepartmentListAsync(int page, int perPage)
        {
            return await _greenSignalContext.Departments.Include(x => x.Location)
                                                        .OrderByDescending(x => x.Name)
                                                        .Skip((page - 1) * perPage)
                                                        .Take(perPage)
                                                        .ToListAsync().ConfigureAwait(false);
        }
    }
}
