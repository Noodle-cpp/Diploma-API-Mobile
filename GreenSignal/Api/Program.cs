using Data.Repositories;
using Domain.Services;
using Api.Middleware;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using System.Reflection;
using System.Text;
using Infrastructure;
using Microsoft.AspNetCore.HttpOverrides;
using System.Net;
using Microsoft.EntityFrameworkCore;
using Data;
using Domain.AttributeServices;
using FileManager;
using MailManager;
using PDFUtility;
using PushNotification;
using Api.BackgroundServices;
using Microsoft.AspNetCore.Localization;
using System.Globalization;

internal class Program
{
    private static async Task Main(string[] args)
    {
        string _corsPolicy = "EnableAll";

        var builder = WebApplication.CreateBuilder(args);

        builder.Services.AddControllers();

        // Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
        builder.Services.AddEndpointsApiExplorer();
        builder.Services.AddSwaggerGen(options =>
        {
            options.SwaggerDoc("v1", new OpenApiInfo
            {
                Version = "v1",
                Title = "Green Signal API",
            });

            var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
            var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
            options.IncludeXmlComments(xmlPath);

            //JWT TOKEN
            options.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
            {
                Description = @"Please provide authorization token to access restricted features.",
                Name = "Authorization",
                In = ParameterLocation.Header,
                Type = SecuritySchemeType.ApiKey,
                Scheme = "Bearer",
                BearerFormat = "JWT",
            });
            options.AddSecurityRequirement(new OpenApiSecurityRequirement()
            {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type = ReferenceType.SecurityScheme,
                    Id = "Bearer"
                }
            },
            new List<string>()
        }
            });

            //API KEY
            options.AddSecurityDefinition("ApiKey", new OpenApiSecurityScheme
            {
                Type = SecuritySchemeType.ApiKey,
                Name = "ApiKey",
                In = ParameterLocation.Header
            });
            options.AddSecurityRequirement(new OpenApiSecurityRequirement
                {
            {
                new OpenApiSecurityScheme
                {
                    Reference = new OpenApiReference
                    {
                        Type = ReferenceType.SecurityScheme,
                        Id = "ApiKey"
                    }
                },
                Array.Empty<string>()
            }
                });
        });

        builder.Services.AddCors(options =>
        {
            options.AddPolicy(name: _corsPolicy,
                            builder =>
                            {
                                builder
                                  .AllowAnyHeader()
                                  .AllowAnyMethod()
                                  .AllowAnyOrigin();
                            });
        });

        builder.Services.Configure<GreenSignalConfigurationOptions>(builder.Configuration);

        builder.Services.Configure<ForwardedHeadersOptions>(options =>
        {
            options.ForwardedHeaders =
                ForwardedHeaders.XForwardedFor | ForwardedHeaders.XForwardedProto;
            options.KnownProxies.Add(IPAddress.Parse("172.17.0.1"));
        });

        AddLifeCycles(builder);

        builder.Services.AddHttpClient();
        builder.Services.AddControllers();

        builder.Services.AddAuthentication(x =>
        {
            x.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
            x.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
            x.DefaultScheme = JwtBearerDefaults.AuthenticationScheme;
        }).AddJwtBearer(o =>
        {
            o.RequireHttpsMetadata = false;
            var key = Encoding.UTF8.GetBytes(builder.Configuration["JWT:Key"]);
            o.SaveToken = true;
            o.TokenValidationParameters = new TokenValidationParameters
            {
                ValidateIssuer = false,
                ValidateAudience = false,
                ValidateLifetime = true,
                ValidateIssuerSigningKey = true,
                ValidIssuer = builder.Configuration["JWT:Issuer"],
                ClockSkew = TimeSpan.Zero,
                ValidAudience = builder.Configuration["JWT:Audience"],
                IssuerSigningKey = new SymmetricSecurityKey(key)
            };
        });
        builder.Services.AddAuthorization(auth =>
        {
            auth.AddPolicy("Bearer", new AuthorizationPolicyBuilder()
                .AddAuthenticationSchemes(JwtBearerDefaults.AuthenticationScheme)
                .RequireAuthenticatedUser().Build());
        });

        builder.Services.AddDbContext<GreenSignalContext>(options =>
            options.UseNpgsql(builder.Configuration.GetConnectionString("Postgres")));

        builder.Services.AddAutoMapper(AppDomain.CurrentDomain.GetAssemblies());
        builder.Services.AddAutoMapper(typeof(Program));

        //TODO: сделать через логи
        try
        {
            builder.Services.AddHostedService<SendMessagesBackground>(); 
            builder.Services.AddHostedService<CheckIncidentsBindingDateBackground>(); 
        }
        catch (Exception)
        {
            Console.WriteLine("Один из фоновых сервисов не может быть выполнен");
        }

        var app = builder.Build();

        // Configure the HTTP request pipeline.
        app.UseForwardedHeaders();

        // Configure the HTTP request pipeline.
        //if (app.Environment.IsDevelopment())
        //{
        app.UseSwagger();
        app.UseSwaggerUI(c => c.SwaggerEndpoint("/swagger/v1/swagger.json", "Api v1"));
        //}

        app.UseHttpsRedirection();
        app.UseJwtMiddleware();

        app.UseCors(_corsPolicy);
        app.UseRouting();

        app.UseAuthentication();
        app.UseAuthorization();

        await ApplyMigrations(app);
        app.MapControllers();

        app.Run();

        static async Task ApplyMigrations(WebApplication app)
        {
            await using var scope = app.Services.CreateAsyncScope();
            using var db = scope.ServiceProvider.GetService<GreenSignalContext>();
            await db.Database.MigrateAsync();
        }

        static void AddLifeCycles(WebApplicationBuilder builder)
        {
            builder.Services.Configure<RequestLocalizationOptions>(options =>
            {
                options.DefaultRequestCulture = new RequestCulture("ru-RU");
                options.SupportedCultures = new List<CultureInfo> { new CultureInfo("ru-RU") };
                options.SupportedUICultures = new List<CultureInfo> { new CultureInfo("ru-RU") };
            });

            //Services
            builder.Services.AddScoped<IInspectorService, InspectorService>();
            builder.Services.AddScoped<ICitizenService, CitizenService>();
            builder.Services.AddScoped<IInspectorSessionService, InspectorSessionService>();
            builder.Services.AddScoped<Domain.Services.IAuthenticationService, Domain.Services.AuthenticationService>();
            builder.Services.AddScoped<ISavedFileService, SavedFileService>();
            builder.Services.AddScoped<IReceiveMessageService, ReceiveMessageService>();
            builder.Services.AddScoped<IIncidentService, IncidentService>();
            builder.Services.AddScoped<IIncidentReportService, IncidentReportService>();
            builder.Services.AddScoped<IIncidentReportAttributeService, IncidentReportAttributeService>();
            builder.Services.AddScoped<IPetitionService, PetitionService>();
            builder.Services.AddScoped<IPetitionAttributeItems, PetitionAttributeItems>();
            builder.Services.AddScoped<IPetitionAttributeValue, PetitionAttributeValue>();
            builder.Services.AddScoped<IPetitionAttributeService, PetitionAttributeService>();
            builder.Services.AddScoped<ILocationService, LocationService>();
            builder.Services.AddScoped<IInspectorScoreService, InspectorScoreService>();
            builder.Services.AddScoped<IDepartmentService, DepartmentService>();

            //Repositories
            builder.Services.AddScoped<IInspectorRepository, InspectorRepository>();
            builder.Services.AddScoped<ICitizenRepository, CitizenRepository>();
            builder.Services.AddScoped<ICodeRepository, CodeRepository>();
            builder.Services.AddScoped<IInspectorSessionRepository, InspectorSessionRepository>();
            builder.Services.AddScoped<ISavedFileRepository, SavedFileRepository>();
            builder.Services.AddScoped<IReceiveMessageRepository, ReceiveMessageRepository>();
            builder.Services.AddScoped<IIncidentRepository, IncidentRepository>();
            builder.Services.AddScoped<IIncidentAttachmentRepository, IncidentAttachmentRepository>();
            builder.Services.AddScoped<IIncidentReportRepository, IncidentReportRepository>();
            builder.Services.AddScoped<IIncidentReportAttachmentRepository, IncidentReportAttachmentRepository>();
            builder.Services.AddScoped<ILocationRepository, LocationRepository>();
            builder.Services.AddScoped<IIncidentReportAttributeRepository, IncidentReportAttributeRepository>();
            builder.Services.AddScoped<IPetitionRepository, PetitionRepository>();
            builder.Services.AddScoped<IDepartmentRepository, DepartmentRepository>();
            builder.Services.AddScoped<IPetitionAttributeRepository, PetitionAttributeRepository>();
            builder.Services.AddScoped<IPetitionAttachmentRepository, PetitionAttachmentRepository>();
            builder.Services.AddScoped<IInspectorScoreRepository, InspectorScoreRepository>();
            builder.Services.AddScoped<IIncidentComplaintRepository, IncidentComplaintRepository>();

            builder.Services.AddScoped<IFileManagerService, FileManagerService>();
            builder.Services.AddScoped<IMailManagerService, MailManagerService>();
            builder.Services.AddScoped<IPdfRender, PdfRender>();
            builder.Services.AddSingleton<INotificationGateway, NotificationGateway>();

            builder.Services.AddScoped<IIncidentReportAttributeValue, IncidentReportAttributeValue>();
            builder.Services.AddScoped<IIncidentReportAttributeItems, IncidentReportAttributeItems>();
        }
    }
}