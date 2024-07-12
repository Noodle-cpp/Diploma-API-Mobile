package com.example.greensignal.domain.repository

import com.example.greensignal.data.remote.dto.response.TokenDto
import com.example.greensignal.domain.model.request.AuthorizeInspector
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.ResponseBody

@ActivityRetainedScoped
interface StorageRepository {
    suspend fun downloadFileFromStorage(path: String): Resource<ResponseBody>
}