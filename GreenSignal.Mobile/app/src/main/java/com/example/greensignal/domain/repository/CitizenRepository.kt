package com.example.greensignal.domain.repository

import com.example.greensignal.data.remote.dto.request.AuthorizeCitizenDto
import com.example.greensignal.data.remote.dto.response.TokenDto
import com.example.greensignal.domain.model.request.GetCode
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped

@ActivityRetainedScoped
interface CitizenRepository {
    suspend fun citizenGetCode(getCode: GetCode) : Resource<String>
    suspend fun citizenLogin(authorizeCitizenDto: AuthorizeCitizenDto) : Resource<TokenDto>
}