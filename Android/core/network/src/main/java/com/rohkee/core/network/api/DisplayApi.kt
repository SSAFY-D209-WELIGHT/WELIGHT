package com.rohkee.core.network.api

import com.rohkee.core.network.model.DisplayRequest
import com.rohkee.core.network.model.DisplayResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DisplayApi {
    // GET
    /**
     * /display/mylist
     * @param sort LATEST, LIKES, DOWNLOADS
     */
    @GET("/display/mylist")
    fun getMyDisplayList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortType") sort: String,
    ): Response<List<DisplayResponse.Short>>

    // /display/{displayId}
    @GET("/display/{displayId}")
    fun getDisplayDetail(
        @Path("displayId") displayId: Long,
    ): Response<DisplayResponse.Detail>

    // /display
    @GET("/display")
    fun getDisplayList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortType") sort: String,
    ): Response<List<DisplayResponse.Short>>

    // /display/{displayId}/edit
    @GET("/display/{displayId}/edit")
    fun getDisplayEdit(
        @Path("displayId") displayId: Long,
    ): Response<DisplayResponse.Editable>

    // /display/search
    @GET("/display/search")
    fun searchDisplayList(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortType") sort: String,
    ): Response<List<DisplayResponse.Short>>

    // TODO : /display/{displayId}/comment

    // POST
    // /display
    @POST("/display")
    fun createDisplay(
        @Body request: DisplayRequest,
    ): Response<DisplayResponse.Posted>

    // /display/{displayId}/duplicate
    @POST("/display/{displayId}/duplicate")
    fun duplicateDisplay(
        @Path("displayId") displayId: Long,
    ): Response<DisplayResponse.Posted>

    // /display/{displayId}/edit
    @POST("/display/{displayId}/edit")
    fun editDisplay(
        @Path("displayId") displayId: Long,
        @Body request: DisplayRequest,
    ): Response<DisplayResponse.Posted>

    // /display/{displayId}/storage
    @POST("/display/{displayId}/storage")
    fun importDisplayToMyStorage(
        @Path("displayId") displayId: Long,
    ): Response<String>

    // TODO : /display/{displayId}/comment

    // /display/{displayId}/like
    @POST("/display/{displayId}/like")
    fun likeDisplay(
        @Path("displayId") displayId: Long,
    ): Response<String>

    // PATCH
    // TODO : /display/{displayId}/comment

    // /display/{displayId}/favorite
    @PATCH("/display/{displayId}/favorite")
    fun favoriteDisplay(
        @Path("displayId") displayId: Long,
    ): Response<String>

    // DELETE
    // /display/{displayId}/storage
    @DELETE("/display/{displayId}/storage")
    fun deleteDisplayFromStorage(
        @Path("displayId") displayId: Long,
    ): Response<String>

    // /display/{displayId} <- not needed

    // TODO : /display/{displayId}/comment

    // /display/{displayId}/like
    @DELETE("/display/{displayId}/like")
    fun unlikeDisplay(
        @Path("displayId") displayId: Long,
    ): Response<String>
}
