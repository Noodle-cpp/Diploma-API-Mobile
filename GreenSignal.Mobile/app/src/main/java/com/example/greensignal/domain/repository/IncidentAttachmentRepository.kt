package com.example.greensignal.domain.repository

import com.example.greensignal.domain.model.request.GetCode
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.MultipartBody
import okhttp3.RequestBody

@ActivityRetainedScoped
interface IncidentAttachmentRepository {
    suspend fun attachFileToIncident(multipartImage: MultipartBody.Part, description: RequestBody, id: String, token: String) : Resource<String>
}