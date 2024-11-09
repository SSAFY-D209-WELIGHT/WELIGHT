package com.rohkee.core.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rohkee.core.network.model.DisplayResponse
import com.rohkee.core.network.repository.SortType
import retrofit2.Response

class DisplayListPagingSource<T : DisplayResponse>(
    private val api: (page: Int, size: Int, sort: String) -> Response<List<T>>,
    private val sortType: SortType,
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val currentPage = params.key ?: 1

            val displays = api(currentPage, 10, sortType.name)

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
