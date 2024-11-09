package com.rohkee.core.network.repositoryImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.api.DisplayApi
import com.rohkee.core.network.apiHandler
import com.rohkee.core.network.model.DisplayRequest
import com.rohkee.core.network.model.DisplayResponse
import com.rohkee.core.network.paging.DisplayMyListPagingSource
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.repository.SortType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DisplayRepositoryImpl @Inject constructor(
    private val displayApi: DisplayApi,
) : DisplayRepository {
    override suspend fun getMyDisplayList(
        sort: SortType,
    ): Flow<PagingData<DisplayResponse.Short>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2,
                ),
            pagingSourceFactory = { DisplayMyListPagingSource(displayApi, sortType = sort) },
        ).flow

    override suspend fun getDisplayDetail(id: Long): ApiResponse<DisplayResponse.Detail> =
        apiHandler {
            displayApi.getDisplayDetail(id)
        }

    override suspend fun getDisplayList(
        page: Int,
        size: Int,
        sort: SortType,
    ): ApiResponse<List<DisplayResponse.Short>> =
        apiHandler {
            displayApi.getDisplayList(
                page = page,
                size = size,
                sort = sort.name,
            )
        }

    override suspend fun getDisplayEdit(id: Long): ApiResponse<DisplayResponse.Editable> =
        apiHandler {
            displayApi.getDisplayEdit(id)
        }

    override suspend fun searchDisplayList(
        page: Int,
        size: Int,
        sort: SortType,
    ): ApiResponse<List<DisplayResponse.Short>> =
        apiHandler {
            displayApi.searchDisplayList(
                page = page,
                size = size,
                sort = sort.name,
            )
        }

    override suspend fun createDisplay(display: DisplayRequest): ApiResponse<DisplayResponse.Posted> =
        apiHandler {
            displayApi.createDisplay(display)
        }

    override suspend fun duplicateDisplay(id: Long): ApiResponse<DisplayResponse.Posted> =
        apiHandler {
            displayApi.duplicateDisplay(id)
        }

    override suspend fun editDisplay(
        id: Long,
        display: DisplayRequest,
    ): ApiResponse<DisplayResponse.Posted> = apiHandler { displayApi.editDisplay(id, display) }

    override suspend fun likeDisplay(id: Long): ApiResponse<String> = apiHandler { displayApi.likeDisplay(id) }

    override suspend fun favoriteDisplay(id: Long): ApiResponse<String> = apiHandler { displayApi.favoriteDisplay(id) }

    override suspend fun deleteDisplayFromStorage(id: Long): ApiResponse<String> = apiHandler { displayApi.deleteDisplayFromStorage(id) }

    override suspend fun unlikeDisplay(id: Long): ApiResponse<String> = apiHandler { displayApi.unlikeDisplay(id) }
}
