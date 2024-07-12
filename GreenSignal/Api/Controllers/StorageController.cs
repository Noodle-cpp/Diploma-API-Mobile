using Api.ViewModels.Requests;
using Microsoft.AspNetCore.Mvc;
using Domain.Services;
using Infrastructure;
using FileManager;

namespace Api.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class StorageController : ControllerBase
    {
        private readonly IFileManagerService _fileManagerService;
        private readonly ISavedFileService _savedFileService;

        public StorageController(IFileManagerService fileManagerService,
                                    ISavedFileService savedFileService)
        {
            _fileManagerService = fileManagerService;
            _savedFileService = savedFileService;
        }

        /// <summary>
        /// Отправляет изображение
        /// </summary>
        /// <param name="photo">Изображение</param>
        /// <returns>Имя файла в хранилище</returns>
        /// <response code="200">Изображение отправлено</response>
        [ProducesResponseType(typeof(string), StatusCodes.Status200OK)]
        // POST <StorageController>/Upload
        [HttpPost("Upload")]
        public async Task<IActionResult> Post([FromForm] CreateFileViewModel photo)
        {
            using Stream fileStream = photo.File.OpenReadStream();

            var memoryStream = new MemoryStream();
            await fileStream.CopyToAsync(memoryStream);
            memoryStream.Seek(0, SeekOrigin.Begin);

            var photoPath = await _fileManagerService.Upload(memoryStream, photo.File.FileName, "IncidentPhotos").ConfigureAwait(false);

            return Ok(photoPath);
        }

        /// <summary>
        /// Отправляет изображение
        /// </summary>
        /// <param name="photo">Изображение</param>
        /// <returns>Имя файла в хранилище</returns>
        /// <response code="200">Изображение отправлено</response>
        [ProducesResponseType(typeof(string), StatusCodes.Status200OK)]
        // POST <StorageController>/Upload
        [HttpPost("Upload/v2")]
        public async Task<IActionResult> Post2([FromForm] CreateFileViewModel photo)
        {
            using Stream fileStream = photo.File.OpenReadStream();
            await _savedFileService.CreateSavedFileAsync(fileStream, photo.File.FileName, Data.Models.SavedFileType.Photo).ConfigureAwait(false);

            return Ok();
        }

        /// <summary>
        /// Получает изображение из хранилища
        /// </summary>
        /// <param name="path">Путь к файлу в хранилище</param>
        /// <returns>Изображение</returns>
        /// <response code="200">Изображение получено</response>
        [ProducesResponseType(typeof(File), StatusCodes.Status200OK)]
        // GET <StorageController>/Download
        [HttpGet("Download/{path}")]
        public async Task<IActionResult> Get(string path)
        {
            var file = await _fileManagerService.Download(path).ConfigureAwait(false);

            //var result = new FileStreamResult(file, $"image/{path[(path.LastIndexOf('.') + 1)..]}")
            //{
            //    FileDownloadName = path[(path.LastIndexOf("%2F") + 1)..]
            //};

            return File(file, $"image/{path[(path.LastIndexOf('.') + 1)..]}", fileDownloadName: path[(path.LastIndexOf("%2F") + 1)..]);
        }
    }
}
