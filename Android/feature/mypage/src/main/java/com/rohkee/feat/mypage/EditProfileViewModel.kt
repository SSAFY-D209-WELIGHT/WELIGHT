package com.rohkee.feat.mypage

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// EditProfileViewModel.kt
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    fun setNickname(newNickname: String) {
        _nickname.value = newNickname
    }

    fun setSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun updateProfile() = viewModelScope.launch {
        _isLoading.value = true
        try {
            // 이미지가 선택되었다면 이미지 업로드
            _selectedImageUri.value?.let { uri ->
                val response = userRepository.updateProfileImage(uri)
                if (response !is ApiResponse.Success) {
                    _error.value = "이미지 업로드 실패"
                    return@launch
                }
            }

            // 닉네임 변경
            val nicknameResponse = userRepository.updateNickname(_nickname.value)
            when (nicknameResponse) {
                is ApiResponse.Success -> {
                    _error.value = null
                }
                is ApiResponse.Error -> {
                    _error.value = nicknameResponse.errorMessage
                }
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "프로필 업데이트 실패"
        } finally {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}