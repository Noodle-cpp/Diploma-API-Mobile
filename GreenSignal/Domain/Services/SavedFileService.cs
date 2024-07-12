using Data;
using Data.Models;
using Data.Repositories;
using Domain.Exceptions;
using FileManager;
using Infrastructure;
using System.Transactions;

namespace Domain.Services
{
    public interface ISavedFileService
    {
        Task<SavedFile> CreateSavedFileAsync(Stream fileStream, string fileName, SavedFileType fileType);
        Task RemoveSavedFileAsync(Guid id);
        Task<byte[]> GetSavedFileById(Guid id);
    }

    public class SavedFileService : ISavedFileService
    {
        private readonly ISavedFileRepository _savedFileRepository;
        private readonly IFileManagerService _fileManagerService;
        private SemaphoreSlim _semaphoreSlim = new SemaphoreSlim(1, 1);


        public SavedFileService(ISavedFileRepository savedFileRepository,
                                IFileManagerService fileManagerService)
        {
            _savedFileRepository = savedFileRepository;
            _fileManagerService = fileManagerService;
        }

        public async Task<SavedFile> CreateSavedFileAsync(Stream fileStream, string fileName, SavedFileType fileType)
        {
            var memoryStream = new MemoryStream();
            await fileStream.CopyToAsync(memoryStream);
            memoryStream.Seek(0, SeekOrigin.Begin);

            var newFile = await _fileManagerService.Upload(memoryStream, fileName, GetPathByType(fileType)).ConfigureAwait(false);

            var savedFile = new SavedFile()
            {
                Id = Guid.NewGuid(),
                CreatedAt = DateTime.UtcNow,
                OrigName = newFile.OriginalName,
                Path = newFile.Path.Replace("/", "%2F"),
                Type = fileType
            };

            await _savedFileRepository.CreateSavedFileAsync(savedFile).ConfigureAwait(false);

            return savedFile;
        }

        public async Task<byte[]> GetSavedFileById(Guid id)
        {
            byte[] savedFileBytes = null;
            await _semaphoreSlim.WaitAsync().ConfigureAwait(false); // Используем SemaphoreSlim для синхронизации доступа к контексту
            try
            {
                var savedFile = await _savedFileRepository.GetSavedFileByIdAsync(id).ConfigureAwait(false) ?? throw new SavedFileNotFoundException();
                using (var file = await _fileManagerService.Download(savedFile.Path).ConfigureAwait(false))
                {
                    file.Position = 0;
                    savedFileBytes = file.ToArray();
                }
            }
            finally
            {
                _semaphoreSlim.Release();
            }
            return savedFileBytes;
        }

        public async Task RemoveSavedFileAsync(Guid id)
        {
            var savedFile = await _savedFileRepository.GetSavedFileByIdAsync(id).ConfigureAwait(false) ?? throw new SavedFileNotFoundException();
            using var transaction = new TransactionScope(TransactionScopeAsyncFlowOption.Enabled);

            await _fileManagerService.Remove(savedFile.Path).ConfigureAwait(false);
            await _savedFileRepository.RemoveSavedFileAsync(savedFile).ConfigureAwait(false);

            transaction.Complete();
            transaction.Dispose();
        }

        private string GetPathByType(SavedFileType fileType)
        {
            switch (fileType)
            {
                case SavedFileType.Photo:
                    return "Photos";
                case SavedFileType.File:
                    return "Files";
                case SavedFileType.Certificate:
                    return "Certificates";
                case SavedFileType.MessageAttachment:
                    return "MessageAttachments";
                default:
                    throw new UnknownFileException();
            }
        }
    }
}
