package com.rohkee.core.network.di

import android.content.Context
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.rohkee.core.network.BuildConfig
import com.rohkee.core.network.api.CheerApi
import com.rohkee.core.network.interceptor.AccessTokenInterceptor
import com.rohkee.core.network.repository.CheerRepository
import com.rohkee.core.network.repositoryImpl.CheerRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json =
            Json {
                isLenient = true
                prettyPrint = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        return Retrofit
            .Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(accessTokenInterceptor: AccessTokenInterceptor) =
        OkHttpClient.Builder().run {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            addNetworkInterceptor(accessTokenInterceptor)
            connectTimeout(20, TimeUnit.SECONDS)
            readTimeout(20, TimeUnit.SECONDS)
            writeTimeout(20, TimeUnit.SECONDS)
            build()
        }

    @Singleton
    @Provides
    fun provideTransferUtility(
        @ApplicationContext context: Context,
    ): TransferUtility {
        val credentials = BasicAWSCredentials(BuildConfig.AWS_ACCESS_KEY, BuildConfig.AWS_SECRET_KEY)
        val region = Region.getRegion(Regions.AP_NORTHEAST_2)
        val awsClient = AmazonS3Client(credentials, region)

        TransferNetworkLossHandler.getInstance(context)

        return TransferUtility
            .builder()
            .context(context)
            .defaultBucket(BuildConfig.BUCKET_NAME)
            .s3Client(awsClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideCheerApi(retrofit: Retrofit): CheerApi {
        return retrofit.create(CheerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCheerRepository(cheerApi: CheerApi): CheerRepository {
        return CheerRepositoryImpl(cheerApi)
    }
}
