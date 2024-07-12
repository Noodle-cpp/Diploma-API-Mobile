using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Data.Migrations
{
    /// <inheritdoc />
    public partial class AddedSignatureToInspector : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<Guid>(
                name: "SignatureId",
                table: "Inspectors",
                type: "uuid",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.CreateIndex(
                name: "IX_Inspectors_SignatureId",
                table: "Inspectors",
                column: "SignatureId");

            migrationBuilder.AddForeignKey(
                name: "FK_Inspectors_SavedFiles_SignatureId",
                table: "Inspectors",
                column: "SignatureId",
                principalTable: "SavedFiles",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Inspectors_SavedFiles_SignatureId",
                table: "Inspectors");

            migrationBuilder.DropIndex(
                name: "IX_Inspectors_SignatureId",
                table: "Inspectors");

            migrationBuilder.DropColumn(
                name: "SignatureId",
                table: "Inspectors");
        }
    }
}
