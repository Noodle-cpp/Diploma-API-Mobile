using Data.Models;
using Data.Repositories;
using Domain.Exceptions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Services
{
    public interface IDepartmentService
    {
        Task<IEnumerable<Department>> GetDepartmentListAsync(int page, int perPage);
        Task<Department?> GetDepartmentByIdAsync(Guid id);
    }

    public class DepartmentService : IDepartmentService
    {
        private readonly IDepartmentRepository _departmentRepository;

        public DepartmentService(IDepartmentRepository departmentRepository)
        {
            _departmentRepository = departmentRepository;
        }

        public async Task<Department?> GetDepartmentByIdAsync(Guid id)
        {
            return await _departmentRepository.GetDepartmentByIdAsync(id).ConfigureAwait(false) ?? throw new DepartmentNotFoundException();
        }

        public async Task<IEnumerable<Department>> GetDepartmentListAsync(int page, int perPage)
        {
            return await _departmentRepository.GetDepartmentListAsync(page, perPage).ConfigureAwait(false);
        }
    }
}
