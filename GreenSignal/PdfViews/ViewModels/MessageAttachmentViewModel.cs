﻿using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PdfViews.ViewModels
{
    public class MessageAttachmentViewModel
    {
        public Guid Id { get; set; }
        public byte[] SavedFileBytes { get; set; }
    }
}
