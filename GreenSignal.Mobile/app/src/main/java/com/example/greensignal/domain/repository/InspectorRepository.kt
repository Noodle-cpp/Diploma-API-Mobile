package com.example.greensignal.domain.repository

import com.example.greensignal.data.remote.dto.request.UpdateInspectorDto
import com.example.greensignal.data.remote.dto.response.InspectorDto
import com.example.greensignal.data.remote.dto.response.MessageDto
import com.example.greensignal.data.remote.dto.response.RatingDto
import com.example.greensignal.data.remote.dto.response.ScoreDto
import com.example.greensignal.data.remote.dto.response.SessionDto
import com.example.greensignal.data.remote.dto.response.TokenDto
import com.example.greensignal.domain.model.request.AuthorizeInspector
import com.example.greensignal.domain.model.request.GetCode
import com.example.greensignal.domain.model.request.UpdateInspectorLocation
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import java.time.LocalDateTime

@ActivityRetainedScoped
interface InspectorRepository {
    suspend fun inspectorLogin(authorizeInspector: AuthorizeInspector) : Resource<TokenDto>
    suspend fun inspectorGetCode(getCode: GetCode) : Resource<String>
    suspend fun inspectorLogout(token: String) : Resource<String>
    suspend fun inspectorGetProfile(token: String) : Resource<InspectorDto>
    suspend fun updateInspectorLocation(token: String, updateInspectorLocation: UpdateInspectorLocation) : Resource<String>
    suspend fun createInspector(fio: RequestBody, phone: RequestBody,
                                certificateId: RequestBody, certificateDate: RequestBody,
                                schoolId: RequestBody, code: RequestBody,
                                firebaseToken: RequestBody, deviceName: RequestBody,
                                inspectorPhoto: MultipartBody.Part, certificatePhoto: MultipartBody.Part) : Resource<TokenDto>
    suspend fun updateInspector(id: String, updateInspector: UpdateInspectorDto, token: String) : Resource<InspectorDto>
    suspend fun updateInspectorPhoto(id: String, inspectorPhoto: MultipartBody.Part, token: String) : Resource<InspectorDto>
    suspend fun updateInspectorSignature(id: String, signaturePhoto: MultipartBody.Part, token: String) : Resource<InspectorDto>
    suspend fun updateInspectorCert(id: String, inspectorCert: MultipartBody.Part, token: String) : Resource<InspectorDto>
    suspend fun registrationInspectorGetCode(getCode: GetCode): Resource<String>

    suspend fun getRating(startDate: String? = null,
                          endDate: String? = null,
                          token: String): Resource<MutableList<RatingDto>>

    suspend fun getInspectorRating(id: String,
                                   startDate: String? = null,
                                   endDate: String? = null,
                                   token: String): Resource<RatingDto>

    suspend fun getScoreHistory(id: String,
                                startDate: String? = null,
                                endDate: String? = null,
                                page: Int?,
                                perPage: Int?,
                                token: String): Resource<MutableList<ScoreDto>>

    suspend fun getInspectorMessages(inspectorId: String, filter: String?, token: String): Resource<MutableList<MessageDto>>

    suspend fun getMessage(id: String, messageId: String, token: String): Resource<MessageDto>

    suspend fun setMessageAsSeen(id: String, messageId: String, token: String): Resource<MessageDto>

    suspend fun getSessions(id: String,
                            token: String): Resource<MutableList<SessionDto>>

    suspend fun deleteSessions(id: String,
                               inspectorId: String,
                               token: String): Resource<String>
}