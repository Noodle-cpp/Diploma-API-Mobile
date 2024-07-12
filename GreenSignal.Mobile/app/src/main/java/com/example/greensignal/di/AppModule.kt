package com.example.greensignal.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.greensignal.common.Constants
import com.example.greensignal.data.remote.GreenSignalApi
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatus
import com.example.greensignal.data.remote.dto.response.IncidentStatus
import com.example.greensignal.data.remote.dto.response.InspectorStatus
import com.example.greensignal.data.remote.dto.response.PetitionKind
import com.example.greensignal.data.remote.dto.response.PetitionStatus
import com.example.greensignal.data.remote.dto.response.ReportType
import com.example.greensignal.data.remote.dto.response.ScoreType
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    class InspectorStatusDeserializer : JsonDeserializer<InspectorStatus> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): InspectorStatus {
            val statusId = json?.asInt ?: InspectorStatus.Created.index
            return InspectorStatus.values().firstOrNull { it.index == statusId } ?: InspectorStatus.Created
        }
    }

    class ScoreTypeDeserializer : JsonDeserializer<ScoreType> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): ScoreType {
            val scoreId = json?.asInt ?: ScoreType.Custom.index
            return ScoreType.values().firstOrNull { it.index == scoreId } ?: ScoreType.Custom
        }
    }

    class IncidentStatusDeserializer : JsonDeserializer<IncidentStatus> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): IncidentStatus {
            val incidentStatusId = json?.asInt ?: IncidentStatus.Draft.index
            return IncidentStatus.values().firstOrNull { it.index == incidentStatusId } ?: IncidentStatus.Draft
        }
    }

    class IncidentKindDeserializer : JsonDeserializer<IncidentKind> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): IncidentKind {
            val incidentKindId = json?.asInt ?: IncidentKind.AirPollution.index
            return IncidentKind.values().firstOrNull { it.index == incidentKindId } ?: IncidentKind.AirPollution
        }
    }

    class IncidentReportKindDeserializer : JsonDeserializer<IncidentReportKind> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): IncidentReportKind {
            val incidentReportKindId = json?.asInt ?: IncidentReportKind.AirPollution.index
            return IncidentReportKind.values().firstOrNull { it.index == incidentReportKindId } ?: IncidentReportKind.AirPollution
        }
    }

    class IncidentReportStatusDeserializer : JsonDeserializer<IncidentReportStatus> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): IncidentReportStatus {
            val incidentReportStatusId = json?.asInt ?: IncidentReportStatus.Draft.index
            return IncidentReportStatus.values().firstOrNull { it.index == incidentReportStatusId } ?: IncidentReportStatus.Draft
        }
    }

    class PetitionStatusDeserializer : JsonDeserializer<PetitionStatus> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): PetitionStatus {
            val petitionStatusId = json?.asInt ?: PetitionStatus.Draft.index
            return PetitionStatus.values().firstOrNull { it.index == petitionStatusId } ?: PetitionStatus.Draft
        }
    }

    class PetitionKindDeserializer : JsonDeserializer<PetitionKind> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): PetitionKind {
            val petitionKindId = json?.asInt ?: PetitionKind.AirPollution.index
            return PetitionKind.values().firstOrNull { it.index == petitionKindId } ?: PetitionKind.AirPollution
        }
    }

    class ReportTypeDeserializer : JsonDeserializer<ReportType> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): ReportType {
            val reportTypeId = json?.asInt ?: ReportType.Unsolvable.index
            return ReportType.values().firstOrNull { it.index == reportTypeId } ?: ReportType.Unsolvable
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(90, TimeUnit.SECONDS)
            .connectTimeout(90, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .registerTypeAdapter(InspectorStatus::class.java, InspectorStatusDeserializer())
            .registerTypeAdapter(ScoreType::class.java, ScoreTypeDeserializer())
            .registerTypeAdapter(IncidentStatus::class.java, IncidentStatusDeserializer())
            .registerTypeAdapter(IncidentKind::class.java, IncidentKindDeserializer())
            .registerTypeAdapter(IncidentReportKind::class.java, IncidentReportKindDeserializer())
            .registerTypeAdapter(IncidentReportStatus::class.java, IncidentReportStatusDeserializer())
            .registerTypeAdapter(PetitionKind::class.java, PetitionKindDeserializer())
            .registerTypeAdapter(PetitionStatus::class.java, PetitionStatusDeserializer())
            .registerTypeAdapter(ReportType::class.java, ReportTypeDeserializer())
            .create()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): GreenSignalApi {
        return retrofit.create(GreenSignalApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences {
        return app.getSharedPreferences("prefs", MODE_PRIVATE)
    }
}