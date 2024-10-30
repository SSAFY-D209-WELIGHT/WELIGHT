package com.rohkee.core.datastore.repository

interface DataStoreRepository {
    suspend fun saveAccessToken(token: String)

    suspend fun deleteAccessToken()

    suspend fun getAccessToken(): String?
}