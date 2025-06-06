﻿// <auto-generated />
using System;
using Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Migrations;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace Data.Migrations
{
    [DbContext(typeof(GreenSignalContext))]
    [Migration("20230829130637_AddedTelegramUserId")]
    partial class AddedTelegramUserId
    {
        /// <inheritdoc />
        protected override void BuildTargetModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("ProductVersion", "7.0.5")
                .HasAnnotation("Relational:MaxIdentifierLength", 63);

            NpgsqlModelBuilderExtensions.UseIdentityByDefaultColumns(modelBuilder);

            modelBuilder.Entity("Data.Models.Citizen", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<string>("FIO")
                        .IsRequired()
                        .HasMaxLength(70)
                        .HasColumnType("character varying(70)");

                    b.Property<string>("Phone")
                        .IsRequired()
                        .HasMaxLength(11)
                        .HasColumnType("character varying(11)");

                    b.Property<string>("TelegramUserId")
                        .HasColumnType("text");

                    b.HasKey("Id");

                    b.ToTable("Citizens");
                });

            modelBuilder.Entity("Data.Models.Code", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<string>("AccessCode")
                        .IsRequired()
                        .HasMaxLength(4)
                        .HasColumnType("character varying(4)");

                    b.Property<int>("CountOfRequests")
                        .HasColumnType("integer");

                    b.Property<DateTime>("CreatedAt")
                        .HasColumnType("timestamp with time zone");

                    b.Property<DateTime>("DateLastRenewal")
                        .HasColumnType("timestamp with time zone");

                    b.Property<string>("Phone")
                        .IsRequired()
                        .HasMaxLength(11)
                        .HasColumnType("character varying(11)");

                    b.HasKey("Id");

                    b.ToTable("Codes");
                });

            modelBuilder.Entity("Data.Models.Department", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<string>("Address")
                        .IsRequired()
                        .HasMaxLength(1000)
                        .HasColumnType("character varying(1000)");

                    b.Property<string>("AliasNames")
                        .IsRequired()
                        .HasMaxLength(1000)
                        .HasColumnType("character varying(1000)");

                    b.Property<string>("Email")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("character varying(100)");

                    b.Property<bool>("IsActive")
                        .HasColumnType("boolean");

                    b.Property<Guid>("LocationId")
                        .HasColumnType("uuid");

                    b.Property<string>("Name")
                        .IsRequired()
                        .HasMaxLength(500)
                        .HasColumnType("character varying(500)");

                    b.HasKey("Id");

                    b.HasIndex("LocationId");

                    b.ToTable("Departments");
                });

            modelBuilder.Entity("Data.Models.Incident", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<string>("Address")
                        .IsRequired()
                        .HasMaxLength(10000)
                        .HasColumnType("character varying(10000)");

                    b.Property<DateTime?>("BindingDate")
                        .HasColumnType("timestamp with time zone");

                    b.Property<DateTime>("CreatedAt")
                        .HasColumnType("timestamp with time zone");

                    b.Property<string>("Description")
                        .IsRequired()
                        .HasMaxLength(10000)
                        .HasColumnType("character varying(10000)");

                    b.Property<Guid?>("InspectorId")
                        .HasColumnType("uuid");

                    b.Property<int>("Kind")
                        .HasColumnType("integer");

                    b.Property<double?>("Lat")
                        .HasColumnType("double precision");

                    b.Property<double?>("Lng")
                        .HasColumnType("double precision");

                    b.Property<Guid>("ReportedById")
                        .HasColumnType("uuid");

                    b.Property<int>("Status")
                        .HasColumnType("integer");

                    b.Property<DateTime?>("UpdatedAt")
                        .HasColumnType("timestamp with time zone");

                    b.HasKey("Id");

                    b.HasIndex("InspectorId");

                    b.HasIndex("ReportedById");

                    b.ToTable("Incidents");
                });

            modelBuilder.Entity("Data.Models.IncidentAttachment", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<Guid>("IncidentId")
                        .HasColumnType("uuid");

                    b.Property<Guid>("SavedFileId")
                        .HasColumnType("uuid");

                    b.HasKey("Id");

                    b.HasIndex("IncidentId");

                    b.HasIndex("SavedFileId");

                    b.ToTable("IncidentAttachments");
                });

            modelBuilder.Entity("Data.Models.IncidentReport", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<int>("AttributesVersion")
                        .HasColumnType("integer");

                    b.Property<DateTime>("CreatedAt")
                        .HasColumnType("timestamp with time zone");

                    b.Property<DateTime>("EndOfInspection")
                        .HasColumnType("timestamp with time zone");

                    b.Property<Guid?>("IncidentId")
                        .IsRequired()
                        .HasColumnType("uuid");

                    b.Property<Guid>("InspectorId")
                        .HasColumnType("uuid");

                    b.Property<int>("Kind")
                        .HasColumnType("integer");

                    b.Property<double>("Lat")
                        .HasColumnType("double precision");

                    b.Property<double>("Lng")
                        .HasColumnType("double precision");

                    b.Property<Guid?>("LocationId")
                        .HasColumnType("uuid");

                    b.Property<DateTime>("ManualDate")
                        .HasColumnType("timestamp with time zone");

                    b.Property<string>("SerialNumber")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<DateTime>("StartOfInspection")
                        .HasColumnType("timestamp with time zone");

                    b.Property<int>("Status")
                        .HasColumnType("integer");

                    b.HasKey("Id");

                    b.HasIndex("IncidentId");

                    b.HasIndex("InspectorId");

                    b.HasIndex("LocationId");

                    b.ToTable("IncidentReports");
                });

            modelBuilder.Entity("Data.Models.IncidentReportAttachment", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<DateTime>("CreatedAt")
                        .HasColumnType("timestamp with time zone");

                    b.Property<string>("Description")
                        .IsRequired()
                        .HasMaxLength(10000)
                        .HasColumnType("character varying(10000)");

                    b.Property<Guid>("IncidentReportId")
                        .HasColumnType("uuid");

                    b.Property<DateTime>("ManualDate")
                        .HasColumnType("timestamp with time zone");

                    b.Property<Guid>("SavedFileId")
                        .HasColumnType("uuid");

                    b.HasKey("Id");

                    b.HasIndex("IncidentReportId");

                    b.HasIndex("SavedFileId");

                    b.ToTable("IncidentReportAttachments");
                });

            modelBuilder.Entity("Data.Models.IncidentReportAttribute", b =>
                {
                    b.Property<string>("Id")
                        .HasColumnType("text");

                    b.Property<bool?>("BoolValue")
                        .HasColumnType("boolean");

                    b.Property<Guid>("IncidentReportId")
                        .HasColumnType("uuid");

                    b.Property<string>("Name")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<double?>("NumberValue")
                        .HasColumnType("double precision");

                    b.Property<string>("StringValue")
                        .HasColumnType("text");

                    b.HasKey("Id");

                    b.HasIndex("IncidentReportId");

                    b.ToTable("IncidentReportAttributes");
                });

            modelBuilder.Entity("Data.Models.Inspector", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<DateTime?>("CertificateDate")
                        .HasColumnType("timestamp with time zone");

                    b.Property<Guid>("CertificateFileId")
                        .HasColumnType("uuid");

                    b.Property<string>("CertificateId")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("character varying(100)");

                    b.Property<DateTime>("CreatedAt")
                        .HasColumnType("timestamp with time zone");

                    b.Property<string>("FIO")
                        .IsRequired()
                        .HasMaxLength(255)
                        .HasColumnType("character varying(255)");

                    b.Property<string>("InternalEmail")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("character varying(100)");

                    b.Property<DateTime?>("LastLatLngAt")
                        .HasColumnType("timestamp with time zone");

                    b.Property<double?>("Lat")
                        .HasColumnType("double precision");

                    b.Property<double?>("Lng")
                        .HasColumnType("double precision");

                    b.Property<int>("Number")
                        .HasColumnType("integer");

                    b.Property<string>("Password")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<string>("Phone")
                        .IsRequired()
                        .HasMaxLength(11)
                        .HasColumnType("character varying(11)");

                    b.Property<Guid?>("PhotoFileId")
                        .HasColumnType("uuid");

                    b.Property<int>("ReviewStatus")
                        .HasColumnType("integer");

                    b.Property<string>("SchoolId")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<string>("TelegramUserId")
                        .HasColumnType("text");

                    b.Property<DateTime?>("UpdatedAt")
                        .HasColumnType("timestamp with time zone");

                    b.HasKey("Id");

                    b.HasIndex("CertificateFileId");

                    b.HasIndex("PhotoFileId");

                    b.ToTable("Inspectors");
                });

            modelBuilder.Entity("Data.Models.InspectorSession", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<DateTime>("CretedAt")
                        .HasColumnType("timestamp with time zone");

                    b.Property<string>("DeviceName")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<string>("FirebaseToken")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<Guid>("InspectorId")
                        .HasColumnType("uuid");

                    b.Property<string>("Ip")
                        .IsRequired()
                        .HasMaxLength(16)
                        .HasColumnType("character varying(16)");

                    b.HasKey("Id");

                    b.HasIndex("InspectorId");

                    b.ToTable("InspectorSessions");
                });

            modelBuilder.Entity("Data.Models.Location", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<string>("Name")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<Guid?>("ParentLocationId")
                        .HasColumnType("uuid");

                    b.HasKey("Id");

                    b.HasIndex("ParentLocationId");

                    b.ToTable("Locations");
                });

            modelBuilder.Entity("Data.Models.MessageAttachment", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<Guid>("ReceiveMessageId")
                        .HasColumnType("uuid");

                    b.Property<Guid>("SavedFileId")
                        .HasColumnType("uuid");

                    b.HasKey("Id");

                    b.HasIndex("ReceiveMessageId");

                    b.HasIndex("SavedFileId");

                    b.ToTable("MessageAttachments");
                });

            modelBuilder.Entity("Data.Models.Petition", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<int>("AttributeVersion")
                        .HasColumnType("integer");

                    b.Property<DateTime>("Date")
                        .HasColumnType("timestamp with time zone");

                    b.Property<Guid>("DepartmentId")
                        .HasColumnType("uuid");

                    b.Property<Guid?>("IncidentReportId")
                        .HasColumnType("uuid");

                    b.Property<Guid>("InspectorId")
                        .HasColumnType("uuid");

                    b.Property<int>("Kind")
                        .HasColumnType("integer");

                    b.Property<Guid?>("ParentPetitionId")
                        .HasColumnType("uuid");

                    b.Property<string>("SerialNumber")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("character varying(100)");

                    b.Property<int>("Status")
                        .HasColumnType("integer");

                    b.HasKey("Id");

                    b.HasIndex("DepartmentId");

                    b.HasIndex("IncidentReportId");

                    b.HasIndex("InspectorId");

                    b.HasIndex("ParentPetitionId");

                    b.ToTable("Petitions");
                });

            modelBuilder.Entity("Data.Models.PetitionAttachment", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<DateTime>("CreatedAt")
                        .HasColumnType("timestamp with time zone");

                    b.Property<string>("Description")
                        .IsRequired()
                        .HasMaxLength(10000)
                        .HasColumnType("character varying(10000)");

                    b.Property<DateTime>("ManualDate")
                        .HasColumnType("timestamp with time zone");

                    b.Property<Guid>("PetitionId")
                        .HasColumnType("uuid");

                    b.Property<Guid>("SavedFileId")
                        .HasColumnType("uuid");

                    b.HasKey("Id");

                    b.HasIndex("PetitionId");

                    b.HasIndex("SavedFileId");

                    b.ToTable("PetitionAttachments");
                });

            modelBuilder.Entity("Data.Models.PetitionAttribute", b =>
                {
                    b.Property<string>("Id")
                        .HasColumnType("text");

                    b.Property<bool?>("BoolValue")
                        .HasColumnType("boolean");

                    b.Property<string>("Name")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<double?>("NumberValue")
                        .HasColumnType("double precision");

                    b.Property<Guid>("PetitionId")
                        .HasColumnType("uuid");

                    b.Property<string>("StringValue")
                        .HasColumnType("text");

                    b.HasKey("Id");

                    b.HasIndex("PetitionId");

                    b.ToTable("PetitionAttributes");
                });

            modelBuilder.Entity("Data.Models.ReceiveMessage", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<string>("Content")
                        .IsRequired()
                        .HasMaxLength(10000)
                        .HasColumnType("character varying(10000)");

                    b.Property<string>("FromAddress")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("character varying(100)");

                    b.Property<string>("FromName")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("character varying(100)");

                    b.Property<Guid>("InspectorId")
                        .HasColumnType("uuid");

                    b.Property<Guid?>("PetitionId")
                        .HasColumnType("uuid");

                    b.Property<bool>("Seen")
                        .HasColumnType("boolean");

                    b.Property<string>("Subject")
                        .IsRequired()
                        .HasMaxLength(100)
                        .HasColumnType("character varying(100)");

                    b.HasKey("Id");

                    b.HasIndex("InspectorId");

                    b.HasIndex("PetitionId");

                    b.ToTable("ReceiveMessages");
                });

            modelBuilder.Entity("Data.Models.SavedFile", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<DateTime>("CreatedAt")
                        .HasColumnType("timestamp with time zone");

                    b.Property<string>("OrigName")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<string>("Path")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<int>("Type")
                        .HasColumnType("integer");

                    b.HasKey("Id");

                    b.ToTable("SavedFiles");
                });

            modelBuilder.Entity("Data.Models.Department", b =>
                {
                    b.HasOne("Data.Models.Location", "Location")
                        .WithMany()
                        .HasForeignKey("LocationId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Location");
                });

            modelBuilder.Entity("Data.Models.Incident", b =>
                {
                    b.HasOne("Data.Models.Inspector", "Inspector")
                        .WithMany()
                        .HasForeignKey("InspectorId");

                    b.HasOne("Data.Models.Citizen", "ReportedBy")
                        .WithMany()
                        .HasForeignKey("ReportedById")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Inspector");

                    b.Navigation("ReportedBy");
                });

            modelBuilder.Entity("Data.Models.IncidentAttachment", b =>
                {
                    b.HasOne("Data.Models.Incident", null)
                        .WithMany("IncidentAttachments")
                        .HasForeignKey("IncidentId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("Data.Models.SavedFile", "SavedFile")
                        .WithMany()
                        .HasForeignKey("SavedFileId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("SavedFile");
                });

            modelBuilder.Entity("Data.Models.IncidentReport", b =>
                {
                    b.HasOne("Data.Models.Incident", "Incident")
                        .WithMany()
                        .HasForeignKey("IncidentId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("Data.Models.Inspector", "Inspector")
                        .WithMany()
                        .HasForeignKey("InspectorId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("Data.Models.Location", "Location")
                        .WithMany()
                        .HasForeignKey("LocationId");

                    b.Navigation("Incident");

                    b.Navigation("Inspector");

                    b.Navigation("Location");
                });

            modelBuilder.Entity("Data.Models.IncidentReportAttachment", b =>
                {
                    b.HasOne("Data.Models.IncidentReport", "IncidentReport")
                        .WithMany("IncidentReportAttachements")
                        .HasForeignKey("IncidentReportId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("Data.Models.SavedFile", "SavedFile")
                        .WithMany()
                        .HasForeignKey("SavedFileId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("IncidentReport");

                    b.Navigation("SavedFile");
                });

            modelBuilder.Entity("Data.Models.IncidentReportAttribute", b =>
                {
                    b.HasOne("Data.Models.IncidentReport", "IncidentReport")
                        .WithMany("IncidentReportAttributes")
                        .HasForeignKey("IncidentReportId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("IncidentReport");
                });

            modelBuilder.Entity("Data.Models.Inspector", b =>
                {
                    b.HasOne("Data.Models.SavedFile", "CertificateFile")
                        .WithMany()
                        .HasForeignKey("CertificateFileId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("Data.Models.SavedFile", "PhotoFile")
                        .WithMany()
                        .HasForeignKey("PhotoFileId");

                    b.Navigation("CertificateFile");

                    b.Navigation("PhotoFile");
                });

            modelBuilder.Entity("Data.Models.InspectorSession", b =>
                {
                    b.HasOne("Data.Models.Inspector", "Inspector")
                        .WithMany()
                        .HasForeignKey("InspectorId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Inspector");
                });

            modelBuilder.Entity("Data.Models.Location", b =>
                {
                    b.HasOne("Data.Models.Location", "ParentLocation")
                        .WithMany()
                        .HasForeignKey("ParentLocationId");

                    b.Navigation("ParentLocation");
                });

            modelBuilder.Entity("Data.Models.MessageAttachment", b =>
                {
                    b.HasOne("Data.Models.ReceiveMessage", "ReceiveMessage")
                        .WithMany("MessageAttachments")
                        .HasForeignKey("ReceiveMessageId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("Data.Models.SavedFile", "SavedFile")
                        .WithMany()
                        .HasForeignKey("SavedFileId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("ReceiveMessage");

                    b.Navigation("SavedFile");
                });

            modelBuilder.Entity("Data.Models.Petition", b =>
                {
                    b.HasOne("Data.Models.Department", "Department")
                        .WithMany()
                        .HasForeignKey("DepartmentId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("Data.Models.IncidentReport", "IncidentReport")
                        .WithMany()
                        .HasForeignKey("IncidentReportId");

                    b.HasOne("Data.Models.Inspector", "Inspector")
                        .WithMany()
                        .HasForeignKey("InspectorId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("Data.Models.Petition", "ParentPetition")
                        .WithMany()
                        .HasForeignKey("ParentPetitionId");

                    b.Navigation("Department");

                    b.Navigation("IncidentReport");

                    b.Navigation("Inspector");

                    b.Navigation("ParentPetition");
                });

            modelBuilder.Entity("Data.Models.PetitionAttachment", b =>
                {
                    b.HasOne("Data.Models.Petition", "Petition")
                        .WithMany("Attachements")
                        .HasForeignKey("PetitionId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("Data.Models.SavedFile", "SavedFile")
                        .WithMany()
                        .HasForeignKey("SavedFileId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Petition");

                    b.Navigation("SavedFile");
                });

            modelBuilder.Entity("Data.Models.PetitionAttribute", b =>
                {
                    b.HasOne("Data.Models.Petition", "Petition")
                        .WithMany("Attributes")
                        .HasForeignKey("PetitionId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("Petition");
                });

            modelBuilder.Entity("Data.Models.ReceiveMessage", b =>
                {
                    b.HasOne("Data.Models.Inspector", "Inspector")
                        .WithMany()
                        .HasForeignKey("InspectorId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("Data.Models.Petition", "Petition")
                        .WithMany("ReceiveMessages")
                        .HasForeignKey("PetitionId");

                    b.Navigation("Inspector");

                    b.Navigation("Petition");
                });

            modelBuilder.Entity("Data.Models.Incident", b =>
                {
                    b.Navigation("IncidentAttachments");
                });

            modelBuilder.Entity("Data.Models.IncidentReport", b =>
                {
                    b.Navigation("IncidentReportAttachements");

                    b.Navigation("IncidentReportAttributes");
                });

            modelBuilder.Entity("Data.Models.Petition", b =>
                {
                    b.Navigation("Attachements");

                    b.Navigation("Attributes");

                    b.Navigation("ReceiveMessages");
                });

            modelBuilder.Entity("Data.Models.ReceiveMessage", b =>
                {
                    b.Navigation("MessageAttachments");
                });
#pragma warning restore 612, 618
        }
    }
}
