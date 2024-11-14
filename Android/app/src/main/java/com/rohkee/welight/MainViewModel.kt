package com.rohkee.welight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.datastore.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _isLoggedIn = MutableLiveData<Boolean?>(null)
    val isLoggedIn: LiveData<Boolean?> = _isLoggedIn

    init {
        checkLoginStatus()
    }

    // 토큰 있는 지 확인
    private fun checkLoginStatus() {
        viewModelScope.launch {
            val token = dataStoreRepository.getAccessToken()
            _isLoggedIn.value = !token.isNullOrEmpty()
        }
    }

    // 토큰 있으면 성공
    fun loginSuccess() {
        _isLoggedIn.value = true
    }
}
