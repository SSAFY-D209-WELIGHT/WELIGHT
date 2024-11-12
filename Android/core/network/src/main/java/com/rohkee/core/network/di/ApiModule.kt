package com.rohkee.core.network.di

import com.rohkee.core.network.api.DisplayApi
import com.rohkee.core.network.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    fun provideDisplayApi(retrofit: Retrofit): DisplayApi = retrofit.create(DisplayApi::class.java)
}
