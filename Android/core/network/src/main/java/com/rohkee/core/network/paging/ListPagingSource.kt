package com.rohkee.core.network.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rohkee.core.network.apiHandler
import com.rohkee.core.network.model.DisplayResponse
import com.rohkee.core.network.model.PageResponse
import com.rohkee.core.network.util.process
import retrofit2.Response

class ListPagingSource<T : DisplayResponse>(
    private val api: suspend (page: Int, size: Int) -> Response<PageResponse<T>>,
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val currentPage = params.key ?: 0

        val result = apiHandler { api(currentPage, 10) }

        return result.process(
            onSuccess = { page ->
                page?.let {
                    LoadResult.Page(
                        data = page.displays,
                        prevKey = if (currentPage == 0) null else currentPage - 1,
                        nextKey = if (page.displays.isEmpty()) null else currentPage + 1,
                    )
                } ?: LoadResult.Error(Exception("null"))
            },
            onError = { _, message ->
                Log.d("TAG", "load: $message")
                LoadResult.Error(Exception(message ?: "error"))
            },
        )
    }
}
