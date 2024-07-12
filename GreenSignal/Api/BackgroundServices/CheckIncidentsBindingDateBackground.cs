using Domain.Services;

namespace Api.BackgroundServices
{
    public class CheckIncidentsBindingDateBackground : BackgroundService
    {
        private readonly IServiceScopeFactory _services;

        public CheckIncidentsBindingDateBackground(IServiceScopeFactory services)
        {
            _services = services;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            using var scope = _services.CreateScope();
            var scopedProcessingService = scope.ServiceProvider.GetRequiredService<IIncidentService>();

            while (!stoppingToken.IsCancellationRequested)
            {
                await scopedProcessingService.DecreaseInspectorsWithOverdueIncidentsAsync().ConfigureAwait(false);
                await Task.Delay((int)(6.048 * Math.Pow(10, 8)), stoppingToken); // 7 дней
            }
        }


        public override async Task StopAsync(CancellationToken stoppingToken)
        {
            await base.StopAsync(stoppingToken);
            await Task.CompletedTask;
        }
    }
}
