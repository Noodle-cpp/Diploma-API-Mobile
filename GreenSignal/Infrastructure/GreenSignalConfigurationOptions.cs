using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Infrastructure
{
    public class GreenSignalConfigurationOptions
    {
        public ConnectionStringsConfigurationOptions ConnectionStrings { get; set; }
        public string ApiKey { get; set; }
        public YandexConfigurationOptions Yandex { get; set; }
        public GotenbergSharpClientConfigurationOptions GotenbergSharpClient { get; set; }
        public DistanceNotificationConfigurationOptions DistanceNotification { get; set; }
        public MailcowConfigurationOptions Mailcow { get; set; }
        public InspectorScore InspectorScore { get; set; }
        public TelegramBotConfigurationOptions TelegramBot { get; set; }
    }

    public class ConnectionStringsConfigurationOptions
    {
        public string Postgres { get; set; }
    }

    public class TelegramBotConfigurationOptions
    {
        public string Token { get; set; }
    }

    public class YandexConfigurationOptions
    {
        public string Token { get; set; }
    }

    public class GotenbergSharpClientConfigurationOptions
    {
        public string ServiceUrl { get; set; }
    }

    public class DistanceNotificationConfigurationOptions
    {
        public double DistanceKm { get; set; }

        public double MaxTimeForLastCoordsUpdateHours { get; set; }
    }

    public class MailcowConfigurationOptions
    {
        public string SmtpServer { get; set; }

        public int SmtpPort { get; set; }

        public int IMapPort { get; set; }

        public string IMapServer { get; set; }

        public string Domain { get; set; }

        public string Key { get; set; }

    }

    public class InspectorScore
    {
        public int OverdueDays { get; set; }
    }
}
