using Domain.Services;
using Infrastructure;
using MailManager;
using MailManager.ViewModels;

namespace Api.BackgroundServices
{
    public class SendMessagesBackground : BackgroundService
    {
        private readonly IServiceScopeFactory _services;

        public SendMessagesBackground(IServiceScopeFactory services)
        {
            _services = services;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            using (var scope = _services.CreateScope())
            {
                var receiveMessageScopedProcessingService = scope.ServiceProvider.GetRequiredService<IReceiveMessageService>();
                var mailManagerScopedProcessingService = scope.ServiceProvider.GetRequiredService<IMailManagerService>();
                var inspectorScopedProcessingService = scope.ServiceProvider.GetRequiredService<IInspectorService>();

                while (!stoppingToken.IsCancellationRequested)
                {
                    var inspectors = await inspectorScopedProcessingService.GetActiveInspectorsAsync().ConfigureAwait(false);
                    foreach (var inspector in inspectors)
                    {
                        var mails = await mailManagerScopedProcessingService.GetMailsByAddress(new EmailAccountViewModel
                        {
                            Address = inspector.InternalEmail,
                            Password = inspector.Password,
                        }).ConfigureAwait(false);
                        await receiveMessageScopedProcessingService.ParseEmailMessagesToDBAsync(mails, inspector.Id).ConfigureAwait(false);
                    }
                    await Task.Delay((int)(18*Math.Pow(10, 5)), stoppingToken); //Каждые полчаса
                }
            }
        }

        public override async Task StopAsync(CancellationToken stoppingToken)
        {
            await base.StopAsync(stoppingToken);
            await Task.CompletedTask;
        }
    }
}
