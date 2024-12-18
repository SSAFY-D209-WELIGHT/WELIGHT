package com.rohkee.core.audio

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {
    @Singleton
    @Provides
    fun provideTempoDetector() = TempoDetector()
}
