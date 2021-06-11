package com.example.bloggingapp.di.auth

import android.content.SharedPreferences
import com.example.bloggingapp.Repository.auth.AuthRepository
import com.example.bloggingapp.Session.SessionManager
import com.example.bloggingapp.api.auth.OpenApiAuthService
import com.example.bloggingapp.persistence.AccountPropertiesDao
import com.example.bloggingapp.persistence.AuthTokenDao
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


@Module
class AuthModule{


    @AuthScope
    @Provides
    fun provideFakeApiService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)


        //This will be of return Type OpenApiAuthService which will return the methods to work on Retrofit
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        sharedPreferences: SharedPreferences,
        editor: SharedPreferences.Editor

    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            sessionManager,
            openApiAuthService,
            sharedPreferences,
            editor

        )
    }

}