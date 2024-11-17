package com.rohkee.core.network.repository

import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.model.CheerRecord
import com.rohkee.core.network.model.CheerResponse

interface CheerRepository {
    suspend fun getCheerRecords(): ApiResponse<List<CheerRecord>>

    suspend fun getCheerRoomList(): ApiResponse<List<CheerResponse>>
}
