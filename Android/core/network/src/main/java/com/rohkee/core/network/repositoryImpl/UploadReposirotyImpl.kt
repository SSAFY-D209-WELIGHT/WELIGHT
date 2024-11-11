package com.rohkee.core.network.repositoryImpl

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.BuildConfig
import com.rohkee.core.network.model.Upload
import com.rohkee.core.network.repository.UploadRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import javax.inject.Inject

class UploadRepositoryImpl @Inject constructor(
    private val transferUtility: TransferUtility,
) : UploadRepository {
    override suspend fun upload(
        key: String,
        file: File,
    ): Flow<ApiResponse<Upload>> =
        callbackFlow {
            val uploadObserver =
                transferUtility.upload(key, file)

            uploadObserver.setTransferListener(
                object : TransferListener {
                    override fun onStateChanged(
                        id: Int,
                        state: TransferState,
                    ) {
                        when (state) {
                            TransferState.COMPLETED -> {
                                val objectUrl = "https://${BuildConfig.BUCKET_NAME}.s3.${BuildConfig.BUCKET_REGION}.amazonaws.com/$key"

                                trySend(ApiResponse.Success(Upload.Completed(objectUrl)))
                                close()
                            }

                            TransferState.CANCELED -> {
                                trySend(
                                    ApiResponse.Error(
                                        errorCode = id,
                                        errorMessage = "업로드 취소",
                                    ),
                                )
                                close()
                            }

                            TransferState.FAILED -> {
                                trySend(
                                    ApiResponse.Error(
                                        errorCode = id,
                                        errorMessage = "업로드 실패",
                                    ),
                                )
                                close()
                            }

                            else -> {}
                        }
                    }

                    override fun onProgressChanged(
                        id: Int,
                        bytesCurrent: Long,
                        bytesTotal: Long,
                    ) {
//                coroutineScope.launch {
//                    emit(ApiResponse.Success(Upload.InProgress(progress = bytesCurrent / bytesTotal.toFloat())))
//                }
                    }

                    override fun onError(
                        id: Int,
                        ex: Exception,
                    ) {
                        trySend(ApiResponse.Error(errorCode = id, errorMessage = ex.message))
                        close()
                    }
                },
            )

            awaitClose {
                uploadObserver.cleanTransferListener()
            }
        }
}
