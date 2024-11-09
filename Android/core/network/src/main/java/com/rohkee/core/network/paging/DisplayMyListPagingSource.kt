package com.rohkee.core.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rohkee.core.network.api.DisplayApi
import com.rohkee.core.network.model.DisplayResponse
import com.rohkee.core.network.repository.SortType

class DisplayMyListPagingSource(
    private val displayApi: DisplayApi,
    private val sortType: SortType,
) : PagingSource<Int, DisplayResponse.Short>() {
    override fun getRefreshKey(state: PagingState<Int, DisplayResponse.Short>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DisplayResponse.Short> {
        return try {
            val currentPage = params.key ?: 1

            val displays =
                displayApi.getMyDisplayList(
                    page = currentPage,
                    size = 10,
                    sort = sortType.name,
                )

            LoadResult.Page(
                data = displays.body()!!,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (displays.body().isNullOrEmpty()) null else currentPage + 1,
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}
