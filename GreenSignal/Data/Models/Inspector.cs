using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Models
{
    public class Inspector
    {
        [Key]
        [Display(Name = "Идентификатор")]
        public Guid Id { get; set; }

        #region User Info

        [MaxLength(255)]
        [Display(Name = "Ф.И.О.")]
        public string FIO { get; set; }

        [MaxLength(11)]
        [RegularExpression(@"^(7([0-6]|[8-9])[0-9]{9})$",
            ErrorMessage = "Неверный формат номера")]
        [Display(Name = "Номер телефона")]
        public string Phone { get; set; }

        [Display(Name = "Статус заявки")]
        public InspectorStatus ReviewStatus { get; set; }

        public string? TelegramUserId { get; set; }
        public string? TelegramChatId { get; set; }

        public Guid? PhotoFileId { get; set; }
        public SavedFile? PhotoFile { get; set; }

        [Display(Name = "Дата создания")]
        public DateTime CreatedAt { get; set; }

        [Display(Name = "Дата обновления")]
        public DateTime? UpdatedAt { get; set; }

        [Display(Name = "Номер")]
        public int Number { get; set; }

        #endregion

        #region Email

        [MaxLength(100)]
        [EmailAddress(ErrorMessage = "Введите корректный E-mail.")]
        [Display(Name = "E-mail")]
        public string InternalEmail { get; set; }

        [Display(Name = "Пароль от почты")]
        [MinLength(8)]
        public string Password { get; set; }

        #endregion

        #region Location

        [Display(Name = "Широта")]
        public double? Lat { get; set; }

        [Display(Name = "Долгота")]
        public double? Lng { get; set; }

        [Display(Name = "Последнее обновление местоположения")]
        public DateTime? LastLatLngAt { get; set; }

        #endregion

        #region Certificate

        [MaxLength(100)]
        [Display(Name = "ID сертификата")]
        public string CertificateId { get; set; }

        [DataType(DataType.Date)]
        [Display(Name = "Дата выдачи сертификата")]
        public DateTime? CertificateDate { get; set; }

        [Required]
        [Display(Name = "Сертификат")]
        public Guid CertificateFileId { get; set; }
        public SavedFile CertificateFile { get; set; }

        [Required]
        public string SchoolId { get; set; }

        public Guid? SignatureId { get; set; }
        public SavedFile? Signature { get; set; }

        #endregion

    }

    public enum InspectorStatus
    {
        [Display(Name = "Создан")]
        Created = 0,
        [Display(Name = "Подтверждение провалено")]
        VerificationFailed = 50,
        [Display(Name = "Активен")]
        Active = 100,
        [Display(Name = "Забанен")]
        Banned = 200
    }
}
