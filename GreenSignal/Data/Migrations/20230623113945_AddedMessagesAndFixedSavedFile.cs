using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Data.Migrations
{
    /// <inheritdoc />
    public partial class AddedMessagesAndFixedSavedFile : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "ContainerName",
                table: "SavedFiles",
                newName: "Path");

            migrationBuilder.CreateTable(
                name: "ReceiveMessages",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    FromName = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    FromAddress = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    InspectorId = table.Column<Guid>(type: "uuid", nullable: false),
                    Subject = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    Content = table.Column<string>(type: "character varying(10000)", maxLength: 10000, nullable: false),
                    Seen = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ReceiveMessages", x => x.Id);
                    table.ForeignKey(
                        name: "FK_ReceiveMessages_Inspectors_InspectorId",
                        column: x => x.InspectorId,
                        principalTable: "Inspectors",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "MessageAttachment",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    ReceiveMessageId = table.Column<Guid>(type: "uuid", nullable: false),
                    SavedFileId = table.Column<Guid>(type: "uuid", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MessageAttachment", x => x.Id);
                    table.ForeignKey(
                        name: "FK_MessageAttachment_ReceiveMessages_ReceiveMessageId",
                        column: x => x.ReceiveMessageId,
                        principalTable: "ReceiveMessages",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_MessageAttachment_SavedFiles_SavedFileId",
                        column: x => x.SavedFileId,
                        principalTable: "SavedFiles",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_MessageAttachment_ReceiveMessageId",
                table: "MessageAttachment",
                column: "ReceiveMessageId");

            migrationBuilder.CreateIndex(
                name: "IX_MessageAttachment_SavedFileId",
                table: "MessageAttachment",
                column: "SavedFileId");

            migrationBuilder.CreateIndex(
                name: "IX_ReceiveMessages_InspectorId",
                table: "ReceiveMessages",
                column: "InspectorId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "MessageAttachment");

            migrationBuilder.DropTable(
                name: "ReceiveMessages");

            migrationBuilder.RenameColumn(
                name: "Path",
                table: "SavedFiles",
                newName: "ContainerName");
        }
    }
}
