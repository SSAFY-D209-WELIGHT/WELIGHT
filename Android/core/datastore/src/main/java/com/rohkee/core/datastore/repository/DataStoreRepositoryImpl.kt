package com.rohkee.core.datastore.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataStoreRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
    ) : DataStoreRepository {
        override suspend fun saveAccessToken(token: String) {
            dataStore.edit { prefs ->
                prefs[ACCESS_TOKEN_KEY] = token
            }
        }

        override suspend fun deleteAccessToken() {
            dataStore.edit { prefs ->
                prefs.remove(ACCESS_TOKEN_KEY)
            }
        }

        override suspend fun getAccessToken(): String? {
            val prefs = dataStore.data.first()
            return prefs[ACCESS_TOKEN_KEY]
        }

        override suspend fun saveUserId(userId: Long) {
            dataStore.edit { prefs ->
                prefs[USER_ID_KEY] = userId
            }
        }

        override suspend fun getUserId(): Long? {
            val prefs = dataStore.data.first()
            return prefs[USER_ID_KEY]
        }

        override suspend fun deleteUserId() {
            dataStore.edit { prefs ->
                prefs.remove(USER_ID_KEY)
            }
        }

        companion object {
            private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
            private val USER_ID_KEY = longPreferencesKey("user_id")
        }
    }
