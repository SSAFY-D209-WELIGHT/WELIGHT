package com.rohkee.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
//import com.rohkee.feat.login.LoginRoute
import com.rohkee.feature.board.BoardRoute
import com.rohkee.feature.board.BoardScreen // 또는 BoardScreen

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
//                LoginRoute()
//                BoardRoute()
                BoardScreen()
            }
        }
    }
}
