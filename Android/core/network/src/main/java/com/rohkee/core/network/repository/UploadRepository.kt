package com.rohkee.core.network.repository

import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.model.Upload
import kotlinx.coroutines.flow.Flow
import java.io.File

interface UploadRepository {
    suspend fun upload(
        key: String,
        file: File,
    ): Flow<ApiResponse<Upload>>

    suspend fun upload(files: List<Pair<String, File>>): Flow<ApiResponse<Upload>>
}
