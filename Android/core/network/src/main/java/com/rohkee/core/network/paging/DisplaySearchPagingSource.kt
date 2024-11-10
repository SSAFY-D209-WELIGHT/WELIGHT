package com.rohkee.core.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rohkee.core.network.apiHandler
import com.rohkee.core.network.model.DisplayResponse
import com.rohkee.core.network.model.PageResponse
import com.rohkee.core.network.repository.SortType
import com.rohkee.core.network.util.process
import retrofit2.Response

class DisplaySearchPagingSource<T : DisplayResponse>(
    private val api: suspend (keyword: String, page: Int, size: Int, sort: String) -> Response<PageResponse<T>>,
    private val keyword: String,
    private val sortType: SortType,
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val currentPage = params.key ?: 1

        val result = apiHandler { api(keyword, currentPage, 10, sortType.name) }

        return result.process(
            onSuccess = { page ->
                page?.let {
                    LoadResult.Page(
                        data = page.displays,
                        prevKey = if (currentPage == 1) null else currentPage - 1,
                        nextKey = if (page.displays.isEmpty()) null else currentPage + 1,
                    )
                } ?: LoadResult.Error(Exception("null"))
            },
            onError = { _, message ->
                LoadResult.Error(Exception(message ?: "error"))
            },
        )
    }
}
