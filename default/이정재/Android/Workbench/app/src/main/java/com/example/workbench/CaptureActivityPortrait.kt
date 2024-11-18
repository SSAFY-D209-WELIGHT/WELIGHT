package com.example.workbench

import com.journeyapps.barcodescanner.CaptureActivity
import android.os.Bundle
import android.content.pm.ActivityInfo

class CaptureActivityPortrait : CaptureActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED  // 세로 모드로 고정
    }
} 