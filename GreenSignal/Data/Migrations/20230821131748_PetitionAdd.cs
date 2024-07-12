using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Data.Migrations
{
    /// <inheritdoc />
    public partial class PetitionAdd : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_MessageAttachment_ReceiveMessages_ReceiveMessageId",
                table: "MessageAttachment");

            migrationBuilder.DropForeignKey(
                name: "FK_MessageAttachment_SavedFiles_SavedFileId",
                table: "MessageAttachment");

            migrationBuilder.DropPrimaryKey(
                name: "PK_MessageAttachment",
                table: "MessageAttachment");

            migrationBuilder.RenameTable(
                name: "MessageAttachment",
                newName: "MessageAttachments");

            migrationBuilder.RenameIndex(
                name: "IX_MessageAttachment_SavedFileId",
                table: "MessageAttachments",
                newName: "IX_MessageAttachments_SavedFileId");

            migrationBuilder.RenameIndex(
                name: "IX_MessageAttachment_ReceiveMessageId",
                table: "MessageAttachments",
                newName: "IX_MessageAttachments_ReceiveMessageId");

            migrationBuilder.AddColumn<Guid>(
                name: "PetitionId",
                table: "ReceiveMessages",
                type: "uuid",
                nullable: true);

            migrationBuilder.AddPrimaryKey(
                name: "PK_MessageAttachments",
                table: "MessageAttachments",
                column: "Id");

            migrationBuilder.CreateTable(
                name: "Departments",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "character varying(500)", maxLength: 500, nullable: false),
                    AliasNames = table.Column<string>(type: "character varying(1000)", maxLength: 1000, nullable: false),
                    Address = table.Column<string>(type: "character varying(1000)", maxLength: 1000, nullable: false),
                    Email = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    IsActive = table.Column<bool>(type: "boolean", nullable: false),
                    LocationId = table.Column<Guid>(type: "uuid", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Departments", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Departments_Locations_LocationId",
                        column: x => x.LocationId,
                        principalTable: "Locations",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Petitions",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    SerialNumber = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    Date = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    DepartmentId = table.Column<Guid>(type: "uuid", nullable: false),
                    IncidentReportId = table.Column<Guid>(type: "uuid", nullable: true),
                    ParentPetitionId = table.Column<Guid>(type: "uuid", nullable: true),
                    InspectorId = table.Column<Guid>(type: "uuid", nullable: false),
                    Status = table.Column<int>(type: "integer", nullable: false),
                    Kind = table.Column<int>(type: "integer", nullable: false),
                    AttributeVersion = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Petitions", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Petitions_Departments_DepartmentId",
                        column: x => x.DepartmentId,
                        principalTable: "Departments",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Petitions_IncidentReports_IncidentReportId",
                        column: x => x.IncidentReportId,
                        principalTable: "IncidentReports",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_Petitions_Inspectors_InspectorId",
                        column: x => x.InspectorId,
                        principalTable: "Inspectors",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Petitions_Petitions_ParentPetitionId",
                        column: x => x.ParentPetitionId,
                        principalTable: "Petitions",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateTable(
                name: "PetitionAttachments",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    SavedFileId = table.Column<Guid>(type: "uuid", nullable: false),
                    Description = table.Column<string>(type: "character varying(10000)", maxLength: 10000, nullable: false),
                    ManualDate = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    PetitionId = table.Column<Guid>(type: "uuid", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_PetitionAttachments", x => x.Id);
                    table.ForeignKey(
                        name: "FK_PetitionAttachments_Petitions_PetitionId",
                        column: x => x.PetitionId,
                        principalTable: "Petitions",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_PetitionAttachments_SavedFiles_SavedFileId",
                        column: x => x.SavedFileId,
                        principalTable: "SavedFiles",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "PetitionAttributes",
                columns: table => new
                {
                    Id = table.Column<string>(type: "text", nullable: false),
                    PetitionId = table.Column<Guid>(type: "uuid", nullable: false),
                    Name = table.Column<string>(type: "text", nullable: false),
                    StringValue = table.Column<string>(type: "text", nullable: true),
                    NumberValue = table.Column<double>(type: "double precision", nullable: true),
                    BoolValue = table.Column<bool>(type: "boolean", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_PetitionAttributes", x => x.Id);
                    table.ForeignKey(
                        name: "FK_PetitionAttributes_Petitions_PetitionId",
                        column: x => x.PetitionId,
                        principalTable: "Petitions",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_ReceiveMessages_PetitionId",
                table: "ReceiveMessages",
                column: "PetitionId");

            migrationBuilder.CreateIndex(
                name: "IX_Departments_LocationId",
                table: "Departments",
                column: "LocationId");

            migrationBuilder.CreateIndex(
                name: "IX_PetitionAttachments_PetitionId",
                table: "PetitionAttachments",
                column: "PetitionId");

            migrationBuilder.CreateIndex(
                name: "IX_PetitionAttachments_SavedFileId",
                table: "PetitionAttachments",
                column: "SavedFileId");

            migrationBuilder.CreateIndex(
                name: "IX_PetitionAttributes_PetitionId",
                table: "PetitionAttributes",
                column: "PetitionId");

            migrationBuilder.CreateIndex(
                name: "IX_Petitions_DepartmentId",
                table: "Petitions",
                column: "DepartmentId");

            migrationBuilder.CreateIndex(
                name: "IX_Petitions_IncidentReportId",
                table: "Petitions",
                column: "IncidentReportId");

            migrationBuilder.CreateIndex(
                name: "IX_Petitions_InspectorId",
                table: "Petitions",
                column: "InspectorId");

            migrationBuilder.CreateIndex(
                name: "IX_Petitions_ParentPetitionId",
                table: "Petitions",
                column: "ParentPetitionId");

            migrationBuilder.AddForeignKey(
                name: "FK_MessageAttachments_ReceiveMessages_ReceiveMessageId",
                table: "MessageAttachments",
                column: "ReceiveMessageId",
                principalTable: "ReceiveMessages",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_MessageAttachments_SavedFiles_SavedFileId",
                table: "MessageAttachments",
                column: "SavedFileId",
                principalTable: "SavedFiles",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_ReceiveMessages_Petitions_PetitionId",
                table: "ReceiveMessages",
                column: "PetitionId",
                principalTable: "Petitions",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_MessageAttachments_ReceiveMessages_ReceiveMessageId",
                table: "MessageAttachments");

            migrationBuilder.DropForeignKey(
                name: "FK_MessageAttachments_SavedFiles_SavedFileId",
                table: "MessageAttachments");

            migrationBuilder.DropForeignKey(
                name: "FK_ReceiveMessages_Petitions_PetitionId",
                table: "ReceiveMessages");

            migrationBuilder.DropTable(
                name: "PetitionAttachments");

            migrationBuilder.DropTable(
                name: "PetitionAttributes");

            migrationBuilder.DropTable(
                name: "Petitions");

            migrationBuilder.DropTable(
                name: "Departments");

            migrationBuilder.DropIndex(
                name: "IX_ReceiveMessages_PetitionId",
                table: "ReceiveMessages");

            migrationBuilder.DropPrimaryKey(
                name: "PK_MessageAttachments",
                table: "MessageAttachments");

            migrationBuilder.DropColumn(
                name: "PetitionId",
                table: "ReceiveMessages");

            migrationBuilder.RenameTable(
                name: "MessageAttachments",
                newName: "MessageAttachment");

            migrationBuilder.RenameIndex(
                name: "IX_MessageAttachments_SavedFileId",
                table: "MessageAttachment",
                newName: "IX_MessageAttachment_SavedFileId");

            migrationBuilder.RenameIndex(
                name: "IX_MessageAttachments_ReceiveMessageId",
                table: "MessageAttachment",
                newName: "IX_MessageAttachment_ReceiveMessageId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_MessageAttachment",
                table: "MessageAttachment",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_MessageAttachment_ReceiveMessages_ReceiveMessageId",
                table: "MessageAttachment",
                column: "ReceiveMessageId",
                principalTable: "ReceiveMessages",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_MessageAttachment_SavedFiles_SavedFileId",
                table: "MessageAttachment",
                column: "SavedFileId",
                principalTable: "SavedFiles",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
