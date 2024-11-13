package com.rohkee.core.network.repositoryImpl

import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.api.CheerApi
import com.rohkee.core.network.model.CheerRecord
import com.rohkee.core.network.repository.CheerRepository
import javax.inject.Inject

class CheerRepositoryImpl @Inject constructor(
    private val cheerApi: CheerApi,
) : CheerRepository {
    override suspend fun getCheerRecords(): ApiResponse<List<CheerRecord>> =
        try {
            val response = cheerApi.getCheerRecords()
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResponse.Success(it)
                } ?: ApiResponse.Error(
                    errorCode = response.code(),
                    errorMessage = "응원 기록을 불러오는데 실패했습니다. 서버에서 데이터를 반환하지 않았습니다.",
                )
            } else {
                ApiResponse.Error(
                    errorCode = response.code(),
                    errorMessage = "응원 기록을 불러오는데 실패했습니다. HTTP 코드: ${response.code()}, 메시지: ${response.message()}",
                )
            }
        } catch (e: Exception) {
            ApiResponse.Error(
                errorMessage = "알 수 없는 에러가 발생했습니다: ${e.localizedMessage}",
            )
        }
}
