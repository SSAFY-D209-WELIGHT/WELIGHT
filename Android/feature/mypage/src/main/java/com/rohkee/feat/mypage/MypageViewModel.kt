package com.rohkee.feat.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohkee.core.network.repository.UserRepository
import com.rohkee.core.network.util.handle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _mypageUIState = MutableStateFlow<MypageUIState>(MypageUIState.Loading)
    val mypageUIState: StateFlow<MypageUIState> = _mypageUIState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userRepository.getUserInfo().handle(
                onSuccess = {
                    if (it != null) {
                        _mypageUIState.value = MypageUIState.Loaded(
                            userName = it.userNickname,
                            userProfileImg = it.userProfileImg,
                        )
                    } else {
                        _mypageUIState.value = MypageUIState.Error("유저 정보를 가져오는데 실패했습니다.")
                    }
                },
                onError = { _, message ->
                    Log.d("TAG", "loadData: $message")
                    _mypageUIState.update { MypageUIState.Error(message) }
                }
            )
        }
    }
}