package com.rohkee.core.network.repository

import androidx.paging.PagingData
import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.model.DisplayRequest
import com.rohkee.core.network.model.DisplayResponse
import kotlinx.coroutines.flow.Flow

enum class SortType {
    LATEST,
    LIKES,
    DOWNLOADS,
}

interface DisplayRepository {
    suspend fun getMyDisplayList(sort: SortType): Flow<PagingData<DisplayResponse.WithFavorite>>

    suspend fun getDisplayDetail(id: Long): ApiResponse<DisplayResponse.Detail>

    suspend fun getDisplayList(sort: SortType): Flow<PagingData<DisplayResponse.WithFavorite>>

    suspend fun getDisplayEdit(id: Long): ApiResponse<DisplayResponse.Editable>

    suspend fun searchDisplayList(
        keyword: String,
        sort: SortType,
    ): Flow<PagingData<DisplayResponse.WithFavorite>>

    suspend fun getLikedDisplayList(): Flow<PagingData<DisplayResponse.Simple>>

    suspend fun createDisplay(display: DisplayRequest): ApiResponse<DisplayResponse.Posted>

    suspend fun duplicateDisplay(id: Long): ApiResponse<DisplayResponse.Posted>

    suspend fun editDisplay(
        id: Long,
        display: DisplayRequest,
    ): ApiResponse<DisplayResponse.Posted>

    suspend fun importDisplayToMyStorage(id: Long): ApiResponse<DisplayResponse.Posted>

    suspend fun likeDisplay(id: Long): ApiResponse<String>

    suspend fun publishDisplay(id: Long): ApiResponse<DisplayResponse.Published>

    suspend fun favoriteDisplay(id: Long): ApiResponse<DisplayResponse.Posted>

    suspend fun deleteDisplayFromStorage(id: Long): ApiResponse<DisplayResponse.Deleted>

    suspend fun unlikeDisplay(id: Long): ApiResponse<String>
}
