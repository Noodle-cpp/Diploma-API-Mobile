using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Data.Migrations
{
    /// <inheritdoc />
    public partial class UpdatedInspector : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "CertificatePhotoFileName",
                table: "Inspectors");

            migrationBuilder.DropColumn(
                name: "PhotoFileName",
                table: "Inspectors");

            migrationBuilder.AddColumn<DateTime>(
                name: "CreatedAt",
                table: "SavedFiles",
                type: "timestamp with time zone",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<int>(
                name: "Type",
                table: "SavedFiles",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<Guid>(
                name: "CertificateFileId",
                table: "Inspectors",
                type: "uuid",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.AddColumn<Guid>(
                name: "PhotoFileId",
                table: "Inspectors",
                type: "uuid",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "SchoolId",
                table: "Inspectors",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.CreateIndex(
                name: "IX_Inspectors_CertificateFileId",
                table: "Inspectors",
                column: "CertificateFileId");

            migrationBuilder.CreateIndex(
                name: "IX_Inspectors_PhotoFileId",
                table: "Inspectors",
                column: "PhotoFileId");

            migrationBuilder.AddForeignKey(
                name: "FK_Inspectors_SavedFiles_CertificateFileId",
                table: "Inspectors",
                column: "CertificateFileId",
                principalTable: "SavedFiles",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Inspectors_SavedFiles_PhotoFileId",
                table: "Inspectors",
                column: "PhotoFileId",
                principalTable: "SavedFiles",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Inspectors_SavedFiles_CertificateFileId",
                table: "Inspectors");

            migrationBuilder.DropForeignKey(
                name: "FK_Inspectors_SavedFiles_PhotoFileId",
                table: "Inspectors");

            migrationBuilder.DropIndex(
                name: "IX_Inspectors_CertificateFileId",
                table: "Inspectors");

            migrationBuilder.DropIndex(
                name: "IX_Inspectors_PhotoFileId",
                table: "Inspectors");

            migrationBuilder.DropColumn(
                name: "CreatedAt",
                table: "SavedFiles");

            migrationBuilder.DropColumn(
                name: "Type",
                table: "SavedFiles");

            migrationBuilder.DropColumn(
                name: "CertificateFileId",
                table: "Inspectors");

            migrationBuilder.DropColumn(
                name: "PhotoFileId",
                table: "Inspectors");

            migrationBuilder.DropColumn(
                name: "SchoolId",
                table: "Inspectors");

            migrationBuilder.AddColumn<string>(
                name: "CertificatePhotoFileName",
                table: "Inspectors",
                type: "character varying(100)",
                maxLength: 100,
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<string>(
                name: "PhotoFileName",
                table: "Inspectors",
                type: "character varying(100)",
                maxLength: 100,
                nullable: false,
                defaultValue: "");
        }
    }
}
