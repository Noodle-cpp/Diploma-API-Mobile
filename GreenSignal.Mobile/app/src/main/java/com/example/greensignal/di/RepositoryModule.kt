package com.example.greensignal.di

import android.app.Application
import android.content.SharedPreferences
import com.example.greensignal.LocationService
import com.example.greensignal.data.remote.GreenSignalApi
import com.example.greensignal.data.repository.CitizenRepositoryImpl
import com.example.greensignal.data.repository.DepartmentRepositoryImpl
import com.example.greensignal.data.repository.IncidentAttachmentRepositoryImpl
import com.example.greensignal.data.repository.IncidentReportRepositoryImpl
import com.example.greensignal.data.repository.IncidentRepositoryImpl
import com.example.greensignal.data.repository.InspectorRepostoryImpl
import com.example.greensignal.data.repository.PetitionRepositoryImpl
import com.example.greensignal.data.repository.StorageRepositoryImpl
import com.example.greensignal.domain.repository.CitizenRepository
import com.example.greensignal.domain.repository.DepartmentRepository
import com.example.greensignal.domain.repository.IncidentAttachmentRepository
import com.example.greensignal.domain.repository.IncidentReportRepository
import com.example.greensignal.domain.repository.IncidentRepository
import com.example.greensignal.domain.repository.InspectorRepository
import com.example.greensignal.domain.repository.PetitionRepository
import com.example.greensignal.domain.repository.StorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideInspectorRepository(api: GreenSignalApi, app: Application, prefs: SharedPreferences): InspectorRepository {
        return InspectorRepostoryImpl(api, app, prefs)
    }

    @Provides
    @Singleton
    fun provideIncidentRepository(api: GreenSignalApi): IncidentRepository {
        return  IncidentRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideIncidentReportRepository(api: GreenSignalApi, app: Application, prefs: SharedPreferences): IncidentReportRepository {
        return  IncidentReportRepositoryImpl(api, app, prefs)
    }

    @Provides
    @Singleton
    fun provideCitizenRepository(api: GreenSignalApi, app: Application, prefs: SharedPreferences): CitizenRepository {
        return  CitizenRepositoryImpl(api, app, prefs)
    }

    @Provides
    @Singleton
    fun provideStorageRepository(api: GreenSignalApi, app: Application): StorageRepository {
        return  StorageRepositoryImpl(api, app)
    }

    @Provides
    @Singleton
    fun provideIncidentAttachmentRepository(api: GreenSignalApi, app: Application, prefs: SharedPreferences): IncidentAttachmentRepository {
        return  IncidentAttachmentRepositoryImpl(api, app, prefs)
    }

    @Provides
    @Singleton
    fun providePetitionRepository(api: GreenSignalApi, app: Application, prefs: SharedPreferences): PetitionRepository {
        return  PetitionRepositoryImpl(api, prefs)
    }

    @Provides
    @Singleton
    fun provideDepartmentRepository(api: GreenSignalApi, app: Application, prefs: SharedPreferences): DepartmentRepository {
        return  DepartmentRepositoryImpl(api, prefs)
    }
}