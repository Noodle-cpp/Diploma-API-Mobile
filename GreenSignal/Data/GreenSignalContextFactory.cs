using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data
{
    public class GreenSignalContextFactory : IDesignTimeDbContextFactory<GreenSignalContext>
    {
        public GreenSignalContext CreateDbContext(string[] args)
        {
            var optionsBuilder = new DbContextOptionsBuilder<GreenSignalContext>();
            optionsBuilder.UseNpgsql("Host=127.0.0.1;Port=5433;User ID=postgres;Password=postgres;database=greensignal;");

            return new GreenSignalContext(optionsBuilder.Options);
        }
    }
}
