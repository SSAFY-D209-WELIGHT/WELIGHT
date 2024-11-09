package com.rohkee.core.network.repository

import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.model.DisplayRequest
import com.rohkee.core.network.model.DisplayResponse

enum class SortType {
    LATEST,
    LIKES,
    DOWNLOADS,
}

interface DisplayRepository {
    suspend fun getMyDisplayList(
        page: Int,
        size: Int,
        sort: SortType,
    ): ApiResponse<List<DisplayResponse.Short>>

    suspend fun getDisplayDetail(id: Long): ApiResponse<DisplayResponse.Detail>

    suspend fun getDisplayList(
        page: Int,
        size: Int,
        sort: SortType,
    ): ApiResponse<List<DisplayResponse.Short>>

    suspend fun getDisplayEdit(id: Long): ApiResponse<DisplayResponse.Editable>

    suspend fun searchDisplayList(
        page: Int,
        size: Int,
        sort: SortType,
    ): ApiResponse<List<DisplayResponse.Short>>

    suspend fun createDisplay(display: DisplayRequest): ApiResponse<DisplayResponse.Posted>

    suspend fun duplicateDisplay(id: Long): ApiResponse<DisplayResponse.Posted>

    suspend fun editDisplay(
        id: Long,
        display: DisplayRequest,
    ): ApiResponse<DisplayResponse.Posted>

    suspend fun likeDisplay(id: Long): ApiResponse<String>

    suspend fun favoriteDisplay(id: Long): ApiResponse<String>

    suspend fun deleteDisplayFromStorage(id: Long): ApiResponse<String>

    suspend fun unlikeDisplay(id: Long): ApiResponse<String>
}
