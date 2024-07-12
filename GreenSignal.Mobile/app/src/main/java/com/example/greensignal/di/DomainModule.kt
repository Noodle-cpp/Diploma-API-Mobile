package com.example.greensignal.di

import com.example.greensignal.presentation.use_case.validation.ValidateAddress
import com.example.greensignal.presentation.use_case.validation.ValidateCadastralNumber
import com.example.greensignal.presentation.use_case.validation.ValidateCertificateId
import com.example.greensignal.presentation.use_case.validation.ValidateCertificateUri
import com.example.greensignal.presentation.use_case.validation.ValidateCode
import com.example.greensignal.presentation.use_case.validation.ValidateCountOfStumps
import com.example.greensignal.presentation.use_case.validation.ValidateDescription
import com.example.greensignal.presentation.use_case.validation.ValidateDiameter
import com.example.greensignal.presentation.use_case.validation.ValidateEvents
import com.example.greensignal.presentation.use_case.validation.ValidateFIO
import com.example.greensignal.presentation.use_case.validation.ValidateFuelType
import com.example.greensignal.presentation.use_case.validation.ValidateInspectorPhotoUri
import com.example.greensignal.presentation.use_case.validation.ValidatePhone
import com.example.greensignal.presentation.use_case.validation.ValidateSchoolId
import com.example.greensignal.presentation.use_case.validation.ValidateSourceDescription
import com.example.greensignal.presentation.use_case.validation.ValidateSquare
import com.example.greensignal.presentation.use_case.validation.ValidateTrashContent
import com.example.greensignal.presentation.use_case.validation.ValidateTerritoryDescription
import com.example.greensignal.presentation.use_case.validation.ValidateTypeOfMiniral
import com.example.greensignal.presentation.use_case.validation.ValidateVolume
import com.example.greensignal.presentation.use_case.validation.ValidateWoodType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    @ViewModelScoped
    fun provideValidateCodeUseCase(): ValidateCode {
     return ValidateCode()
    }

    @Provides
    @ViewModelScoped
    fun provideValidatePhoneUseCase(): ValidatePhone {
     return ValidatePhone()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateDescriptionUseCase(): ValidateDescription {
     return ValidateDescription()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateCertificateIdUseCase(): ValidateCertificateId {
     return ValidateCertificateId()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateCertificateUriUseCase(): ValidateCertificateUri {
     return ValidateCertificateUri()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateInspectorPhotoUriUseCase(): ValidateInspectorPhotoUri {
     return ValidateInspectorPhotoUri()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateSchoolIdUseCase(): ValidateSchoolId {
     return ValidateSchoolId()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateFIOUseCase(): ValidateFIO {
     return ValidateFIO()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateCadastralNumberUseCase(): ValidateCadastralNumber {
     return ValidateCadastralNumber()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateSourceOfPollutionUseCase(): ValidateTrashContent {
     return ValidateTrashContent()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateSourceDescriptionUseCase(): ValidateSourceDescription {
     return ValidateSourceDescription()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateFuelTypeUseCase(): ValidateFuelType {
     return ValidateFuelType()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateAddressUseCase(): ValidateAddress {
     return ValidateAddress()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateEventsUseCase(): ValidateEvents {
     return ValidateEvents()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateTerritoryDescriptionUseCase(): ValidateTerritoryDescription {
     return ValidateTerritoryDescription()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateCountOfStumpsUseCase(): ValidateCountOfStumps {
     return ValidateCountOfStumps()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateDiameterUseCase(): ValidateDiameter {
     return ValidateDiameter()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateSquareUseCase(): ValidateSquare {
     return ValidateSquare()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateTypeOfMiniralUseCase(): ValidateTypeOfMiniral {
     return ValidateTypeOfMiniral()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateVolumeUseCase(): ValidateVolume {
     return ValidateVolume()
    }

    @Provides
    @ViewModelScoped
    fun provideValidateWoodTypeUseCase(): ValidateWoodType {
     return ValidateWoodType()
    }
}