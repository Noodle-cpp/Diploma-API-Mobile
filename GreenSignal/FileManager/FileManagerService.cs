using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using Microsoft.Extensions.Options;
using System.IO;
using System.Net.Http.Headers;
using FileManager.Exceptions;
using System.Reflection.Metadata;
using Newtonsoft.Json;
using static System.Net.WebRequestMethods;
using Infrastructure;
using FileManager.ViewModels;
using System.IO.Pipes;

namespace FileManager
{
    public interface IFileManagerService
    {
        public Task<FileViewModel> Upload(MemoryStream fileStream, string fileName, string folder);
        public Task<MemoryStream> Download(string path);
        public Task Remove(string path);
    }

    public class FileManagerService : IFileManagerService
    {
        private readonly string _token;
        private readonly IHttpClientFactory _httpClientFactory;

        public FileManagerService(IHttpClientFactory httpClientFactory, IOptions<GreenSignalConfigurationOptions> options)
        {
            _httpClientFactory = httpClientFactory;
            _token = options.Value.Yandex.Token;
        }

        /// <summary>
        /// Скачивает файл с диска
        /// </summary>
        /// <param name="path"></param>
        /// <returns>Поток файла</returns>
        /// <exception cref="DownloadFileException"></exception>
        public async Task<MemoryStream> Download(string path)
        {
            var httpClient = _httpClientFactory.CreateClient();

            httpClient.DefaultRequestHeaders.TryAddWithoutValidation("Authorization", "OAuth " + _token);
            string downloadUrl = $"https://cloud-api.yandex.net/v1/disk/resources/download?path=%2FGreenSignal%2F{path}";

            var response = await httpClient.GetAsync(downloadUrl).ConfigureAwait(false);
            if (!response.IsSuccessStatusCode)
                throw new DownloadFileException("Документ не найден");

            var responseContentString = await response.Content.ReadAsStringAsync().ConfigureAwait(false);
            var responseContent = JsonConvert.DeserializeObject<Dictionary<string, string>>(responseContentString);
            var uploadUrl = responseContent["href"];

            var downloadResponse = await httpClient.GetAsync(uploadUrl).ConfigureAwait(false);
            if (!downloadResponse.IsSuccessStatusCode)
                throw new DownloadFileException("Невозможно скачать документ");

            var fileStream = await downloadResponse.Content.ReadAsStreamAsync().ConfigureAwait(false);

            var memoryStream = new MemoryStream();
            await fileStream.CopyToAsync(memoryStream);
            memoryStream.Seek(0, SeekOrigin.Begin);

            return memoryStream;
        }
 
        public async Task Remove(string path)
        {
            var httpClient = _httpClientFactory.CreateClient();

            httpClient.DefaultRequestHeaders.TryAddWithoutValidation("Authorization", "OAuth " + _token);

            string url = $"https://cloud-api.yandex.net/v1/disk/resources?path=GreenSignal%2F{path}&permanently=true";
            var response = await httpClient.DeleteAsync(url).ConfigureAwait(false);

            if (!response.IsSuccessStatusCode)
            {
                throw new RemoveFileException();
            }
        }

        /// <summary>
        /// Загружает файл на диск
        /// </summary>
        /// <param name="fileStream"></param>
        /// <param name="fileName"></param>
        /// <param name="folder"></param>
        /// <returns>Новый путь к файлу</returns>
        /// <exception cref="UploadFileException"></exception>
        public async Task<FileViewModel> Upload(MemoryStream fileStream, string fileName, string folder)
        {
            var httpClient = _httpClientFactory.CreateClient();

            httpClient.DefaultRequestHeaders.TryAddWithoutValidation("Authorization", "OAuth " + _token);

            var newFileName = $"{Guid.NewGuid()}.{fileName[(fileName.LastIndexOf('.') + 1)..]}";

            string url = $"https://cloud-api.yandex.net/v1/disk/resources/upload?path=GreenSignal/{folder}/{newFileName}&overwrite=true";
            var response = await httpClient.GetAsync(url).ConfigureAwait(false);

            if (!response.IsSuccessStatusCode)
            {
                throw new UploadFileException();
            }

            var responseContentString = await response.Content.ReadAsStringAsync().ConfigureAwait(false);
            var responseContent = JsonConvert.DeserializeObject<Dictionary<string, string>>(responseContentString);
            var uploadUrl = responseContent["href"];

            var content = new StreamContent(fileStream);
            var uploadResponse = await httpClient.PutAsync(uploadUrl, content);
            
            if (!uploadResponse.IsSuccessStatusCode)
                throw new UploadFileException();

            fileStream.Close();
            return new FileViewModel()
            {
                OriginalName = fileName,
                Path = $"{folder}/{newFileName}"
            };
        }
    }
}
