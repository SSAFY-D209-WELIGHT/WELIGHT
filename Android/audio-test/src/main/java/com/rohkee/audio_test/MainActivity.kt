
package com.rohkee.audio_test

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.PitchProcessor
import be.tarsos.dsp.onsets.OnsetHandler
import be.tarsos.dsp.onsets.ComplexOnsetDetector
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private var dispatcher: AudioDispatcher? = null
    private var currentPitch by mutableStateOf("0 Hz")
    private var currentTempo by mutableStateOf("0 BPM")
    private var lastBeatTime = 0L
    private var beatIntervals = mutableListOf<Long>()
    private val BEAT_HISTORY_SIZE = 4

    // 오디오 녹음 관련 변수
    private var audioRecord: AudioRecord? = null
    private val SAMPLE_RATE = 44100
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
    private var isRecording = false
    private var audioData = mutableListOf<Byte>()
    private var permissionGranted = false

    // 권한 요청을 처리하기 위한 변수
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
        if (isGranted) {
            startAudioProcessing()
        } else {
            Toast.makeText(this, "오디오 녹음 권한이 필요합니다", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permissionGranted = checkAudioPermission()
        if (!permissionGranted) {
            requestAudioPermission()
        }

        setContent {
            MainScreen(
                pitch = currentPitch,
                tempo = currentTempo,
                onStartClick = { checkAndRequestAudioPermission() },
                onStopClick = { stopAudioProcessing() },
                onStartRecording = { startRecording() },
                onStopRecording = { stopRecording() },
                onPlayRecording = { playRecording() }
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
                startAudioProcessing()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startAudioProcessing() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)

        val pdh = PitchDetectionHandler { result: PitchDetectionResult, _: AudioEvent ->
            val pitchInHz = result.pitch
            runOnUiThread {
                processPitch(pitchInHz)
            }
        }

        val pitchProcessor = PitchProcessor(
            PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
            22050f,
            1024,
            pdh
        )

        val onsetHandler = OnsetHandler { _: Double, _: Double ->
            val currentTime = System.currentTimeMillis()
            if (lastBeatTime > 0) {
                val interval = currentTime - lastBeatTime
                if (interval > 10) {
                    beatIntervals.add(interval)
                    if (beatIntervals.size > BEAT_HISTORY_SIZE) {
                        beatIntervals.removeAt(0)
                    }
                    calculateTempo()
                }
            }
            lastBeatTime = currentTime
        }

        // 값이 낮으면 더 많은 이벤트를 감지하고, 값이 높으면 노이즈를 줄이면서 강한 비트만 감지
        val onsetDetector = ComplexOnsetDetector(1024).apply {
            setThreshold(0.1) //default 0.2
            setHandler(onsetHandler)
        }

        dispatcher?.addAudioProcessor(pitchProcessor)
        dispatcher?.addAudioProcessor(onsetDetector)

        Thread(dispatcher, "Audio Thread").start()
    }

    private fun calculateTempo() {
        if (beatIntervals.size >= 4) {
            val mean = beatIntervals.average()
            val standardDeviation = beatIntervals.map { (it - mean) * (it - mean) }.average().let { Math.sqrt(it) }

            val filteredIntervals = beatIntervals.filter { interval ->
                Math.abs(interval - mean) <= 2 * standardDeviation // 이상치 필터링
            }

            val averageInterval = filteredIntervals.average()
            val bpm = (60000.0 / averageInterval).roundToInt()

            if (bpm in 40..220) {
                runOnUiThread {
                    currentTempo = "$bpm BPM"
                }
            }
        }
    }

//    private fun calculateTempo() {
//        if (beatIntervals.size >= 1) {
//            val averageInterval = beatIntervals.average()
//            val bpm = (60000.0 / averageInterval).roundToInt()
//
//            if (bpm in 40..220) {
//                runOnUiThread {
//                    currentTempo = "$bpm BPM"
//                }
//            }
//        }
//    }
//
//    private fun calculateTempo() {
//        if (beatIntervals.size >= 4) {
//            val medianInterval = beatIntervals.sorted().let {
//                it[it.size / 2]
//            } // 중간값을 사용
//            val bpm = (60000.0 / medianInterval).roundToInt()
//
//            if (bpm in 40..220) {
//                runOnUiThread {
//                    currentTempo = "$bpm BPM"
//                }
//            }
//        }
//    }


    private fun startRecording() {
        if (!isRecording && permissionGranted) {
            if (audioRecord == null) {
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    bufferSize
                )
            }

            audioData.clear()
            audioRecord?.startRecording()
            isRecording = true

            CoroutineScope(Dispatchers.IO).launch {
                val buffer = ByteArray(bufferSize)
                while (isRecording) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        audioData.addAll(buffer.take(read))
                    }
                }
            }
        }
    }

    private fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
    }

    private fun playRecording() {
        if (audioData.isNotEmpty()) {
            val audioTrack = AudioTrack.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(SAMPLE_RATE)
                        .setEncoding(AUDIO_FORMAT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(audioData.size)
                .build()

            audioTrack.write(audioData.toByteArray(), 0, audioData.size)
            audioTrack.play()
        } else {
            Toast.makeText(this, "녹음된 데이터가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopAudioProcessing() {
        dispatcher?.stop()
        dispatcher = null
        beatIntervals.clear()
        lastBeatTime = 0
        currentTempo = "0 BPM"
    }

    private fun processPitch(pitchInHz: Float) {
        currentPitch = if (pitchInHz > 0) {
            val note = when {
                pitchInHz in 16.35..17.32 -> "C0"
                pitchInHz in 17.32..18.35 -> "C#0"
                pitchInHz in 18.35..19.45 -> "D0"
                pitchInHz in 19.45..20.60 -> "D#0"
                pitchInHz in 20.60..21.83 -> "E0"
                else -> "Unknown"
            }
            "$note (${String.format("%.1f", pitchInHz)} Hz)"
        } else {
            "No pitch detected"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudioProcessing()
        audioRecord?.release()
        audioRecord = null
    }
}

@Composable
fun MainScreen(
    pitch: String,
    tempo: String,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRecording: () -> Unit,
    modifier: Modifier = Modifier
) {


    //    val blinkInterval = 1000L // (1초 = 1000ms)
    // tempo 값을 정수로 변환하고 밀리초 단위로 1분에 해당하는 간격을 설정
    val blinkInterval = try {
        val temp = tempo.replace(" BPM", "").toInt()/60 // 60000 밀리초를 BPM으로 나누어 깜빡임 간격 계산
        1000/temp.toLong()
    } catch (e: Exception) {
        1000L  // 변환 실패 시 기본값 설정 (1초)
    }


    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 상단 50%에 기존 UI 표시
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)  // 화면 상단 40%에만 표시
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Current Pitch:", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = pitch, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Current Tempo:", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = tempo, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(32.dp))

                // 피치/템포 감지 버튼
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = onStartClick) {
                        Text("Start Detection")
                    }
                    Button(onClick = onStopClick) {
                        Text("Stop Detection")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 녹음/재생 버튼
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = onStartRecording) {
                        Text("녹음 시작")
                    }
                    Button(onClick = onStopRecording) {
                        Text("녹음 중지")
                    }
                    Button(onClick = onPlayRecording) {
                        Text("재생")
                    }
                }
            }

            // 하단 60%에 BlinkingScreen 추가
            BlinkingScreen(
                blinkInterval = blinkInterval,  // tempo 값을 기반으로 깜빡임 간격 설정
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .align(Alignment.BottomCenter)
            )
        }
    }//end of Scaffold
}

// BlinkingScreen 컴포저블 함수
//@Composable
//fun BlinkingScreen(blinkInterval: Long, modifier: Modifier = Modifier) {
//    var isBlinking by remember { mutableStateOf(true) }
//    var currentColor by remember { mutableStateOf(Color.Transparent) } // 초기 색상을 완전 투명으로 설정
//
//    // 깜빡임 효과를 위한 LaunchedEffect 블록
//    LaunchedEffect(isBlinking, blinkInterval) {
//        while (isBlinking) {
//            // currentColor 값을 반투명 검은색과 완전 투명으로 번갈아 설정
//            currentColor = if (currentColor == Color.Transparent)
//                Color.Black.copy(alpha = 0.9f) else Color.Transparent
//            delay(blinkInterval)
//        }
//    }
//
//    // 깜빡임 배경을 적용할 Box
//    Box(modifier = modifier.background(currentColor))
//}

@Composable
fun BlinkingScreen(blinkInterval: Long = 50, modifier: Modifier = Modifier) {
    var isBlinking by remember { mutableStateOf(true) }
    var currentColor by remember { mutableStateOf(Color.Transparent) } // 초기 색상을 완전 투명으로 설정

    // 깜빡임 효과를 위한 LaunchedEffect
    LaunchedEffect(isBlinking, blinkInterval) {
        while (isBlinking) {
            // currentColor 값을 반투명 검은색과 완전 투명으로 번갈아 설정
            currentColor = if (currentColor == Color.Transparent)
                Color.Black.copy(alpha = 0.9f) else Color.Transparent
            delay(blinkInterval)
        }
    }

    // 첫 번째 Box: 배경 이미지와 깜빡임 레이어
    Box(modifier = modifier.fillMaxSize()) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.yh), // 배경으로 사용할 이미지
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 깜빡임 효과를 적용할 Box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(currentColor) // 깜빡임 배경색을 currentColor로 설정
        )
    }
}
