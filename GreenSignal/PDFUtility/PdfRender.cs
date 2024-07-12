using Infrastructure;
using Microsoft.AspNetCore.Mvc.ModelBinding;
using Microsoft.AspNetCore.Mvc.Razor;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.AspNetCore.Mvc.ViewFeatures;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Options;
using Razor.Templating.Core;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

namespace PDFUtility
{
    public interface IPdfRender
    {
        public Task<string> GetHtmlFromRazor(string viewName, object model);
        public Task<Stream> GeneratePdf(string html);
        public byte[] StreamToByteArray(Stream stream);
    }

    public class PdfRender : IPdfRender
    {
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly string url;

        public PdfRender(IHttpClientFactory httpClientFactory, IOptions<GreenSignalConfigurationOptions> configuration)
        {
            _httpClientFactory = httpClientFactory;
            url = configuration.Value.GotenbergSharpClient.ServiceUrl + $"/forms/chromium/convert/html";//configuration["GotenbergSharpClient:ServiceUrl"] + $"/forms/chromium/convert/html";
        }

        /// <summary>
        /// Получает Html из Razor View
        /// </summary>
        /// <param name="viewName"></param>
        /// <param name="model"></param>
        /// <returns></returns>
        public async Task<string> GetHtmlFromRazor(string viewName, object model)
        {
            return await RazorTemplateEngine.RenderAsync(viewName, model);
        }

        /// <summary>
        /// Создаёт pdf файл из html 
        /// </summary>
        /// <param name="html"></param>
        /// <returns></returns>
        public async Task<Stream> GeneratePdf(string html)
        {
            var httpClient = _httpClientFactory.CreateClient();

            try
            {
                using var content = new MultipartFormDataContent();

                byte[] byteArray = Encoding.ASCII.GetBytes(html);
                var htmlContent = new ByteArrayContent(byteArray);
                htmlContent.Headers.ContentType = MediaTypeHeaderValue.Parse("application/pdf");

                content.Add(htmlContent, "index.html", "index.html");

                var response = await httpClient.PostAsync(new Uri(url), content);

                if (response.IsSuccessStatusCode)
                    return await response.Content.ReadAsStreamAsync();
                else throw new FileLoadException();
            }
            catch (Exception ex)
            {
                throw new FileLoadException(ex.Message);
            }
        }

        /// <summary>
        /// Переписывает поток полученноо изображения в массив byte, чтобы отобразить изображение на странице 
        /// </summary>
        /// <param name="stream"></param>
        /// <returns></returns>
        public byte[] StreamToByteArray(Stream stream)
        {
            using var memoryStream = new MemoryStream();
            stream.CopyTo(memoryStream);
            return memoryStream.ToArray();
        }
    }
}
