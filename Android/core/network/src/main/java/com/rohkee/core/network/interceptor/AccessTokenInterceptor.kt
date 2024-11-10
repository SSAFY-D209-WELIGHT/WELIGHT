package com.rohkee.core.network.interceptor

import com.rohkee.core.datastore.repository.DataStoreRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AccessTokenInterceptor @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        val accessToken: String? =
            runBlocking {
                dataStoreRepository.getAccessToken()
            }
        val request =
            if (accessToken == null) {
                requestBuilder.build()
            } else {
                requestBuilder
                    .header("Authorization", accessToken)
                    .build()
            }
        return chain.proceed(request)
    }
}
