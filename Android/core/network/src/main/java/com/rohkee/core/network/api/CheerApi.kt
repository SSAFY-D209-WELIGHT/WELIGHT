package com.rohkee.core.network.api

import com.rohkee.core.network.model.CheerRecord
import com.rohkee.core.network.model.CheerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CheerApi {
    @GET("/api/cheer/records")
    suspend fun getCheerRecords(): Response<List<CheerRecord>>

    @GET("/api/cheer")
    suspend fun getCheerRoomList(
        @Query("latitude")
        latitude: Double,
        @Query("longitude")
        longitude: Double,
        @Query("radius")
        radius: Int = 1,
    ): Response<List<CheerResponse>>
}
