package com.example.greensignal.data.remote

import com.example.greensignal.data.remote.dto.request.AuthorizeCitizenDto
import com.example.greensignal.data.remote.dto.request.AuthorizeInspectorDto
import com.example.greensignal.data.remote.dto.request.CreateIncidentReportDto
import com.example.greensignal.data.remote.dto.request.CreatePetitionDto
import com.example.greensignal.data.remote.dto.request.GetCodeDto
import com.example.greensignal.data.remote.dto.request.UpdateIncidentReportAttributeDto
import com.example.greensignal.data.remote.dto.request.UpdateIncidentReportDto
import com.example.greensignal.data.remote.dto.request.UpdateInspectorDto
import com.example.greensignal.data.remote.dto.request.UpdateInspectorLocationDto
import com.example.greensignal.data.remote.dto.request.UpdatePetitionAttributeDto
import com.example.greensignal.data.remote.dto.request.UpdatePetitionDto
import com.example.greensignal.data.remote.dto.response.DepartmentDto
import com.example.greensignal.data.remote.dto.response.IncidentDto
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentReportAttributeItemDto
import com.example.greensignal.data.remote.dto.response.IncidentReportDto
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatisticDto
import com.example.greensignal.data.remote.dto.response.IncidentReportStatus
import com.example.greensignal.data.remote.dto.response.IncidentStatisticDto
import com.example.greensignal.data.remote.dto.response.IncidentStatus
import com.example.greensignal.data.remote.dto.response.InspectorDto
import com.example.greensignal.data.remote.dto.response.MessageDto
import com.example.greensignal.data.remote.dto.response.PetitionDto
import com.example.greensignal.data.remote.dto.response.RatingDto
import com.example.greensignal.data.remote.dto.response.ReportType
import com.example.greensignal.data.remote.dto.response.ScoreDto
import com.example.greensignal.data.remote.dto.response.SessionDto
import com.example.greensignal.data.remote.dto.response.TokenDto
import com.example.greensignal.domain.model.request.CreateIncident
import com.example.greensignal.domain.model.request.GetCode
import com.example.greensignal.domain.model.request.UpdateIncidentReport
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface GreenSignalApi {
    ///
    /// Inspector
    ///

    @Headers("Content-Type: application/json")
    @POST("Inspectors/Authorize")
    suspend fun inspectorLogin(@Body body: AuthorizeInspectorDto): TokenDto

    @Headers("Content-Type: application/json")
    @POST("Inspectors/Logout")
    suspend fun inspectorLogout(@Header("Authorization") token: String)

    @Headers("Content-Type: application/json")
    @POST("Inspectors/GetCode")
    suspend fun getInspectorCode(@Body body: GetCodeDto)

    @Headers("Content-Type: application/json")
    @GET("Inspectors/Profile")
    suspend fun getInspectorProfile(@Header("Authorization") token: String): InspectorDto

    @Headers("Content-Type: application/json")
    @PUT("Inspectors/Location")
    suspend fun updateInspectorLocation(@Header("Authorization") token: String, @Body body: UpdateInspectorLocationDto): String

    @Multipart
    @POST("Inspectors")
    suspend fun createInspector(@Part("fio") fio: RequestBody,
                                @Part("phone") phone: RequestBody,
                                @Part("certificateId") certificateId: RequestBody,
                                @Part("certificateDate") certificateDate: RequestBody,
                                @Part("schoolId") schoolId: RequestBody,
                                @Part("code") code: RequestBody,
                                @Part("firebaseToken") firebaseToken: RequestBody,
                                @Part("deviceName") deviceName: RequestBody,
                                @Part inspectorPhoto: MultipartBody.Part,
                                @Part certificatePhoto: MultipartBody.Part
    ) : TokenDto

    @Headers("Content-Type: application/json")
    @PUT("Inspectors/{id}")
    suspend fun updateInspector(@Path("id") id: String,
                                @Body updateInspector: UpdateInspectorDto,
                                @Header("Authorization") token: String
    ) : InspectorDto

    @Multipart
    @PUT("Inspectors/{id}/Photo")
    suspend fun updateInspectorPhoto(@Path("id") id: String,
                                     @Part inspectorPhoto: MultipartBody.Part,
                                     @Header("Authorization") token: String
    ) : InspectorDto

    @Multipart
    @PUT("Inspectors/{id}/Signature")
    suspend fun updateInspectorSignature(@Path("id") id: String,
                                        @Part signaturePhoto: MultipartBody.Part,
                                        @Header("Authorization") token: String
    ) : InspectorDto

    @Multipart
    @PUT("Inspectors/{id}/Certificate")
    suspend fun updateInspectorCert(@Path("id") id: String,
                                    @Part inspectorCert: MultipartBody.Part,
                                    @Header("Authorization") token: String
    ) : InspectorDto

    @Headers("Content-Type: application/json")
    @POST("Inspectors/Registration/GetCode")
    suspend fun registrationInspectorGetCode(@Body body: GetCodeDto)

    @Headers("Content-Type: application/json")
    @GET("Inspectors/Rating")
    suspend fun getRating(@Query("startDate") startDate: String? = null,
                          @Query("endDate") endDate: String? = null,
                          @Header("Authorization") token: String): MutableList<RatingDto>

    @Headers("Content-Type: application/json")
    @GET("Inspectors/{inspectorId}/Rating")
    suspend fun getInspectorRating(@Path("inspectorId") inspectorId: String,
                                      @Query("startDate") startDate: String? = null,
                                      @Query("endDate") endDate: String? = null,
                                      @Header("Authorization") token: String): RatingDto

    @Headers("Content-Type: application/json")
    @GET("Inspectors/{inspectorId}/Scores")
    suspend fun getScoreHistory(@Path("inspectorId") inspectorId: String,
                                @Query("startDate") startDate: String?,
                                @Query("endDate") endDate: String?,
                                @Query("page") page: Int?,
                                @Query("perPage") perPage: Int?,
                                @Header("Authorization") token: String): MutableList<ScoreDto>

    @Headers("Content-Type: application/json")
    @GET("Inspectors/{inspectorId}/Messages")
    suspend fun getInspectorMessages(@Path("inspectorId") inspectorId: String,
                                     @Header("Authorization") token: String,
                                     @Query("filter") filter: String?): MutableList<MessageDto>

    @Headers("Content-Type: application/json")
    @GET("Inspectors/{id}/Messages/{messageId}")
    suspend fun getMessage(@Path("id") id: String,
                         @Path("messageId") messageId: String,
                         @Header("Authorization") token: String): MessageDto
    @Headers("Content-Type: application/json")
    @GET("Inspectors/{id}/Sessions")
    suspend fun getSessions(@Path("id") id: String,
                           @Header("Authorization") token: String): MutableList<SessionDto>

    @Headers("Content-Type: application/json")
    @DELETE("Inspectors/{inspectorId}/Sessions/{id}")
    suspend fun deleteSessions(@Path("id") id: String,
                               @Path("inspectorId") inspectorId: String,
                               @Header("Authorization") token: String)

    @Headers("Content-Type: application/json")
    @PUT("Inspectors/{id}/Messages/{messageId}/MarkAsSeen")
    suspend fun setMessageAsSeen(@Path("id") id: String,
                                 @Path("messageId") messageId: String,
                                 @Header("Authorization") token: String): MessageDto

    ///
    /// Citizen
    ///

    @Headers("Content-Type: application/json")
    @POST("Citizens/Authorize")
    suspend fun citizenLogin(@Body body: AuthorizeCitizenDto): TokenDto

    @Headers("Content-Type: application/json")
    @POST("Citizens/GetCode")
    suspend fun getCitizenCode(@Body body: GetCode)


    ///
    /// Incident
    ///

    @Headers("Content-Type: application/json")
    @GET("Incidents/Statistic")
    suspend fun getIncidentStatistic(): IncidentStatisticDto

    @Headers("Content-Type: application/json")
    @POST("Incidents")
    suspend fun createIncident(@Body body: CreateIncident, @Header("Authorization") token: String): IncidentDto


    @Headers("Content-Type: application/json")
    @PUT("Incidents/{id}/apply")
    suspend fun applyIncident(@Path(value = "id") id: String,
                                @Header("Authorization") token: String): IncidentDto

    @Multipart
    @PUT("Incidents/{id}/file/attach")
    suspend fun createIncidentAttachment(@Path(value = "id") id: String,
                                         @Header("Authorization") token: String,
                                         @Part file: MultipartBody.Part ,
                                         @Part("description") description: RequestBody)

    @Headers("Content-Type: application/json")
    @PUT("Incidents/{id}/Report")
    suspend fun reportIncident(@Path(value = "id") id: String,
                               @Header("Authorization") token: String,
                               @Query("reportType") reportType: ReportType)

    @Headers("Content-Type: application/json")
    @GET("Inspectors/Incidents")
    suspend fun getIncidentsNerby(@Query("page") page: Int?,
                                  @Query("perPage") perPage: Int?,
                                  @Query("isNerby") isNerby: Boolean?,
                                  @Query("incidentKind") incidentKind: IncidentKind?,
                                  @Header("Authorization") token: String): MutableList<IncidentDto>

    @Headers("Content-Type: application/json")
    @GET("Inspectors/{id}/Incidents")
    suspend fun getIncidents(@Path(value = "id") id: String,
                             @Query("page") page: Int?,
                             @Query("perPage") perPage: Int?,
                             @Query("incidentKind") incidentKind: IncidentKind?,
                             @Query("incidentStatus") incidentStatus: IncidentStatus?,
                             @Header("Authorization") token: String): MutableList<IncidentDto>

    @Headers("Content-Type: application/json")
    @GET("Citizens/Incidents/My")
    suspend fun getIncidentsForCitizen(@Query("page") page: Int?,
                                         @Query("perPage") perPage: Int?,
                                         @Header("Authorization") token: String): MutableList<IncidentDto>

    @Headers("Content-Type: application/json")
    @GET("Incidents/{id}")
    suspend fun getIncident(@Path(value = "id") id: String, @Header("Authorization") token: String): IncidentDto

    @Headers("Content-Type: application/json")
    @PUT("/Incidents/{id}/attach")
    suspend fun getIncidentInWork(@Path(value = "id") id: String, @Header("Authorization") token: String): IncidentDto

    ///
    /// IncidentReport
    ///

    @Headers("Content-Type: application/json")
    @GET("IncidentReports/Statistic")
    suspend fun getIncidentReportsStatistic(): IncidentReportStatisticDto

    @Headers("Content-Type: application/json")
    @POST("IncidentReports")
    suspend fun createIncidentReport(@Body createIncidentReportDto: CreateIncidentReportDto,
                                     @Header("Authorization") token: String): IncidentReportDto

    @Headers("Content-Type: application/json")
    @PUT("IncidentReports/{id}/attributes/update")
    suspend fun updateIncidentReportAttributes(@Body updateIncidentReportAttributes: MutableList<UpdateIncidentReportAttributeDto>,
                                               @Path(value = "id") id: String,
                                               @Header("Authorization") token: String): IncidentReportDto

    @Headers("Content-Type: application/json")
    @GET("IncidentReports/{id}")
    suspend fun getIncidentReport(@Path(value = "id") id: String,
                                  @Header("Authorization") token: String): IncidentReportDto

    @Headers("Content-Type: application/json")
    @GET("IncidentReports")
    suspend fun getIncidentReportList(@Query("page") page: Int?,
                                      @Query("perPage") perPage: Int?,
                                      @Query("incidentReportKind") incidentReportKind: IncidentReportKind?,
                                      @Query("incidentReportStatus") incidentReportStatus: IncidentReportStatus?,
                                      @Header("Authorization") token: String): MutableList<IncidentReportDto>

    @Headers("Content-Type: application/json")
    @GET("IncidentReports/{id}/pdf")
    suspend fun getIncidentReportPdf(@Path("id") id: String,
                                      @Header("Authorization") token: String): ResponseBody


    @Headers("Content-Type: application/json")
    @DELETE("IncidentReports/{id}")
    suspend fun deleteIncidentReport(@Path("id") id: String,
                                     @Header("Authorization") token: String): ResponseBody

    @Headers("Content-Type: application/json")
    @GET("IncidentReports/attributes")
    suspend fun getIncidentReportAttributesItem(@Query(value = "incidentReportKind") incidentReportKind: IncidentReportKind,
                                                   @Query(value = "attributeVersion") attributeVersion: Int,
                                                   @Header("Authorization") token: String): MutableList<IncidentReportAttributeItemDto>

    @Headers("Content-Type: application/json")
    @PUT("IncidentReports/{id}")
    suspend fun updateIncidentReport(@Path(value = "id") id: String,
                                     @Body updateIncidentReport: UpdateIncidentReportDto,
                                     @Header("Authorization") token: String): IncidentReportDto

    @Headers("Content-Type: application/json")
    @PUT("IncidentReports/{incidentReportId}/attachments/{id}/detach")
    suspend fun detachAttachmentIncidentReport(@Path(value = "id") id: String,
                                               @Path(value = "incidentReportId") incidentReportId: String,
                                               @Header("Authorization") token: String): IncidentReportDto

    @Multipart
    @PUT("IncidentReports/{id}/attachments/attach")
    suspend fun attachmentAddIncidentReport(@Path(value = "id") id: String,
                                            @Header("Authorization") token: String,
                                            @Part file: MultipartBody.Part,
                                            @Part("description") description: RequestBody,
                                            @Part("manualDate") manualDate: RequestBody)

    ///
    /// Storage
    ///

    @Streaming
    @GET("/Storage/Download/{path}")
    suspend fun downloadFileFromStorage(@Path(value = "path") path: String): ResponseBody

    ///
    /// Petition
    ///

    @Headers("Content-Type: application/json")
    @POST("Petitions")
    suspend fun createPetition(@Body createPetitionDto: CreatePetitionDto,
                               @Header("Authorization") token: String): PetitionDto

    @Headers("Content-Type: application/json")
    @GET("Petitions/{id}")
    suspend fun getPetition(@Path(value = "id") id: String,
                              @Header("Authorization") token: String): PetitionDto

    @Headers("Content-Type: application/json")
    @DELETE("Petitions/{id}")
    suspend fun removePetition(@Path(value = "id") id: String,
                               @Header("Authorization") token: String)

    @Headers("Content-Type: application/json")
    @PUT("Petitions/{id}/attributes/update")
    suspend fun updatePetitionAttribute(@Path(value = "id") id: String,
                                        @Body updatePetitionAttributes: MutableList<UpdatePetitionAttributeDto>,
                                        @Header("Authorization") token: String)

    @Headers("Content-Type: application/json")
    @PUT("Petitions/{id}/receiveMessages/{receiveMessageId}/attach")
    suspend fun attachMessageToPetition(@Path(value = "id") id: String,
                                        @Path(value = "receiveMessageId") receiveMessageId: String,
                                        @Header("Authorization") token: String)
    @Headers("Content-Type: application/json")
    @PUT("Petitions/{id}/receiveMessages/{receiveMessageId}/detach")
    suspend fun detachMessageToPetition(@Path(value = "id") id: String,
                                        @Path(value = "receiveMessageId") receiveMessageId: String,
                                        @Header("Authorization") token: String)
    @Headers("Content-Type: application/json")
    @PUT("Petitions/{id}/close/successed")
    suspend fun closeSuccessPetition(@Path(value = "id") id: String,
                                     @Header("Authorization") token: String)
    @Headers("Content-Type: application/json")
    @PUT("Petitions/{id}")
    suspend fun updatePetition(@Path(value = "id") id: String,
                               @Body updatePetition: UpdatePetitionDto,
                               @Header("Authorization") token: String): PetitionDto
    @Headers("Content-Type: application/json")
    @PUT("Petitions/{id}/close/failed")
    suspend fun closeFailedPetition(@Path(value = "id") id: String,
                                    @Header("Authorization") token: String)

    @Headers("Content-Type: application/json")
    @GET("Petitions")
    suspend fun getPetitionList(@Query("page") page: Int?,
                                @Query("perPage") perPage: Int?,
                                @Header("Authorization") token: String): MutableList<PetitionDto>
    @Headers("Content-Type: application/json")
    @PUT("Petitions/{id}/sent")
    suspend fun sentPetition(@Path(value = "id") id: String,
                             @Header("Authorization") token: String)

    ///
    /// Department
    ///

    @Headers("Content-Type: application/json")
    @GET("Departments")
    suspend fun getDepartmentList(@Query("page") page: Int?,
                                  @Query("perPage") perPage: Int?,
                                  @Header("Authorization") token: String): MutableList<DepartmentDto>

    @Headers("Content-Type: application/json")
    @GET("Departments/{id}")
    suspend fun getDepartment(@Path(value = "id") id: String,
                              @Header("Authorization") token: String): DepartmentDto
}