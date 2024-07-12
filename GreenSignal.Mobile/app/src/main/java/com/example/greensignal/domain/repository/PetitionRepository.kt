package com.example.greensignal.domain.repository

import com.example.greensignal.data.remote.dto.request.CreatePetitionDto
import com.example.greensignal.data.remote.dto.request.UpdatePetitionAttributeDto
import com.example.greensignal.data.remote.dto.request.UpdatePetitionDto
import com.example.greensignal.data.remote.dto.response.PetitionDto
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

@ActivityRetainedScoped
interface PetitionRepository {
    suspend fun createPetition(createPetition: CreatePetitionDto, token: String): Resource<PetitionDto>
    suspend fun getPetition(id: String, token: String): Resource<PetitionDto>
    suspend fun getPetitionList(page: Int?, perPage: Int?, token: String): Resource<MutableList<PetitionDto>>
    suspend fun updatePetitionAttribute(id: String,
                                        updatePetitionAttributes: MutableList<UpdatePetitionAttributeDto>,
                                        token: String): Resource<String>
    suspend fun removePetition(id: String,
                               token: String): Resource<String>

    suspend fun attachMessageToPetition(id: String,
                                        receiveMessageId: String,
                                        token: String): Resource<String>

    suspend fun detachMessageFromPetition(id: String,
                                          receiveMessageId: String,
                                          token: String): Resource<String>

    suspend fun closeSuccessPetition(id: String,
                                     token: String): Resource<String>

    suspend fun sentPetition(id: String,
                             token: String): Resource<String>
    suspend fun closeFailedPetition(id: String,
                                    token: String): Resource<String>


    suspend fun updatePetition(id: String,
                               updatePetitionDto: UpdatePetitionDto,
                               token: String): Resource<PetitionDto>
}