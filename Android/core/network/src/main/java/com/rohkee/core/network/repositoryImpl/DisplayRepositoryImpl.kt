package com.rohkee.core.network.repositoryImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.api.DisplayApi
import com.rohkee.core.network.apiHandler
import com.rohkee.core.network.model.DisplayRequest
import com.rohkee.core.network.model.DisplayResponse
import com.rohkee.core.network.paging.ListPagingSource
import com.rohkee.core.network.repository.DisplayRepository
import com.rohkee.core.network.repository.SortType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DisplayRepositoryImpl @Inject constructor(
    private val displayApi: DisplayApi,
) : DisplayRepository {
    override suspend fun getMyDisplayList(sort: SortType): Flow<PagingData<DisplayResponse.WithFavorite>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2,
                ),
            pagingSourceFactory = {
                ListPagingSource<DisplayResponse.WithFavorite>(
                    api = { page, size ->
                        displayApi.getMyDisplayList(
                            page,
                            size,
                            sort = sort.name,
                        )
                    },
                )
            },
        ).flow

    override suspend fun getDisplayDetail(id: Long): ApiResponse<DisplayResponse.Detail> = apiHandler { displayApi.getDisplayDetail(id) }

    override suspend fun getDisplayList(sort: SortType): Flow<PagingData<DisplayResponse.WithFavorite>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2,
                ),
            pagingSourceFactory = {
                ListPagingSource<DisplayResponse.WithFavorite>(
                    api = { page, size -> displayApi.getDisplayList(page, size, sort = sort.name) },
                )
            },
        ).flow

    override suspend fun getDisplayEdit(id: Long): ApiResponse<DisplayResponse.Editable> = apiHandler { displayApi.getDisplayEdit(id) }

    override suspend fun importDisplayToMyStorage(id: Long): ApiResponse<DisplayResponse.Posted> =
        apiHandler { displayApi.importDisplayToMyStorage(id) }

    override suspend fun searchDisplayList(
        keyword: String,
        sort: SortType,
    ): Flow<PagingData<DisplayResponse.WithFavorite>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2,
                ),
            pagingSourceFactory = {
                ListPagingSource<DisplayResponse.WithFavorite>(
                    api = { page, size ->
                        displayApi.searchDisplayList(
                            keyword,
                            page,
                            size,
                            sort = SortType.LATEST.name,
                        )
                    },
                )
            },
        ).flow

    override suspend fun getLikedDisplayList(): Flow<PagingData<DisplayResponse.Simple>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2,
                    initialLoadSize = 10,
                    enablePlaceholders = false,
                ),
            pagingSourceFactory = {
                ListPagingSource<DisplayResponse.Simple>(
                    api = { page, size -> displayApi.getLikedDisplayList(page, size) },
                )
            },
        ).flow

    override suspend fun createDisplay(display: DisplayRequest): ApiResponse<DisplayResponse.Posted> =
        apiHandler { displayApi.createDisplay(display) }

    override suspend fun duplicateDisplay(id: Long): ApiResponse<DisplayResponse.Posted> = apiHandler { displayApi.duplicateDisplay(id) }

    override suspend fun editDisplay(
        id: Long,
        display: DisplayRequest,
    ): ApiResponse<DisplayResponse.Posted> = apiHandler { displayApi.editDisplay(id, display) }

    override suspend fun publishDisplay(id: Long): ApiResponse<DisplayResponse.Published> = apiHandler { displayApi.publishDisplay(id) }

    override suspend fun likeDisplay(id: Long): ApiResponse<DisplayResponse.Liked> = apiHandler { displayApi.likeDisplay(id) }

    override suspend fun favoriteDisplay(id: Long): ApiResponse<DisplayResponse.Posted> = apiHandler { displayApi.favoriteDisplay(id) }

    override suspend fun deleteDisplayFromStorage(id: Long): ApiResponse<DisplayResponse.Deleted> =
        apiHandler { displayApi.deleteDisplayFromStorage(id) }

    override suspend fun unlikeDisplay(id: Long): ApiResponse<DisplayResponse.Liked> = apiHandler { displayApi.unlikeDisplay(id) }
}
