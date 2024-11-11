package com.rohkee.audio_test

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.rohkee.audio_test.TempoDetector
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private lateinit var tempoDetector: TempoDetector
    private var currentTempo by mutableStateOf("0 BPM")
    private var permissionGranted = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
        if (isGranted) {
            startTempoDetection()
        } else {
            Toast.makeText(this, "오디오 녹음 권한이 필요합니다", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tempoDetector = TempoDetector(this)

        permissionGranted = checkAudioPermission()
        if (!permissionGranted) {
            requestAudioPermission()
        }

        setContent {
            MainScreen(
                tempo = currentTempo,
                onStartClick = { checkAndRequestAudioPermission() },
                onStopClick = { stopTempoDetection() }
            )
        }
    }

    private fun checkAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun checkAndRequestAudioPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startTempoDetection()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startTempoDetection() {
        tempoDetector.startDetection(
            onTempoUpdate = { bpm ->
                currentTempo = bpm
            },
            onBeatDetected = {
                //
                // 비트 감지될 때마다 수행할 작업
                // 예: 진동, 사운드 재생 등
            }
        )
    }

    private fun stopTempoDetection() {
        tempoDetector.stopDetection()
        currentTempo = "0 BPM"
    }

    override fun onDestroy() {
        super.onDestroy()
        tempoDetector.stopDetection()
    }
}
//////////////////////

@Composable
fun MainScreen(
    tempo: String,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Current Tempo", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = tempo, fontSize = 24.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Start and Stop Detection buttons
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = onStartClick) {
                        Text("Start Detection")
                    }
                    Button(onClick = onStopClick) {
                        Text("Stop Detection")
                    }
                }
            }
        }
    )
}