package com.rohkee.core.network.repository

import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.model.CheerRecord

interface CheerRepository {
    suspend fun getCheerRecords(): ApiResponse<List<CheerRecord>>
}
