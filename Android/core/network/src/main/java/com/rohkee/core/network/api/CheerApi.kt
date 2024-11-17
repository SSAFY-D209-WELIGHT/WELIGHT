package com.rohkee.core.network.api

import com.rohkee.core.network.model.CheerRecord
import com.rohkee.core.network.model.CheerResponse
import retrofit2.Response
import retrofit2.http.GET

interface CheerApi {
    @GET("/api/cheer/records")
    suspend fun getCheerRecords(): Response<List<CheerRecord>>

    @GET("/api/cheer")
    suspend fun getCheerRoomList(): Response<List<CheerResponse>>
}
