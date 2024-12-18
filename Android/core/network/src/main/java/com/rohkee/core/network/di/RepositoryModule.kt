package com.rohkee.core.network.di

import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.repository.UploadRepository
import com.rohkee.core.network.repository.UserRepository
import com.rohkee.core.network.repositoryImpl.DisplayRepositoryImpl
import com.rohkee.core.network.repositoryImpl.UploadRepositoryImpl
import com.rohkee.core.network.repositoryImpl.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun provideUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    fun provideDisplayRepository(displayRepositoryImpl: DisplayRepositoryImpl): DisplayRepository

    @Binds
    @Singleton
    fun provideUploadRepository(uploadRepositoryImpl: UploadRepositoryImpl): UploadRepository

}
