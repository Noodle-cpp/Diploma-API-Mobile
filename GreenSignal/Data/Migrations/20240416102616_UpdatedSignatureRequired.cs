using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Data.Migrations
{
    /// <inheritdoc />
    public partial class UpdatedSignatureRequired : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Inspectors_SavedFiles_SignatureId",
                table: "Inspectors");

            migrationBuilder.AlterColumn<Guid>(
                name: "SignatureId",
                table: "Inspectors",
                type: "uuid",
                nullable: true,
                oldClrType: typeof(Guid),
                oldType: "uuid");

            migrationBuilder.AddForeignKey(
                name: "FK_Inspectors_SavedFiles_SignatureId",
                table: "Inspectors",
                column: "SignatureId",
                principalTable: "SavedFiles",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Inspectors_SavedFiles_SignatureId",
                table: "Inspectors");

            migrationBuilder.AlterColumn<Guid>(
                name: "SignatureId",
                table: "Inspectors",
                type: "uuid",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"),
                oldClrType: typeof(Guid),
                oldType: "uuid",
                oldNullable: true);

            migrationBuilder.AddForeignKey(
                name: "FK_Inspectors_SavedFiles_SignatureId",
                table: "Inspectors",
                column: "SignatureId",
                principalTable: "SavedFiles",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
