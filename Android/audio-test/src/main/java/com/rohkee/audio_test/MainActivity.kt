package com.rohkee.audio_test

import android.os.Bundle //welight
import android.Manifest //오디오 권한 확인
import android.content.pm.PackageManager //오디오 권한 확인
import androidx.activity.ComponentActivity //Compose를 활용한 UI와 권한 요청을 설정하는 데 사용
import androidx.activity.compose.setContent //Compose를 활용한 UI와 권한 요청을 설정하는 데 사용
import androidx.activity.result.contract.ActivityResultContracts //Compose를 활용한 UI와 권한 요청을 설정하는 데 사용
import androidx.activity.enableEdgeToEdge //welight
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize //welight
import androidx.compose.foundation.layout.padding //welight
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold //welight
import androidx.compose.material3.Text //welight
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable //welight
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier //welight
import androidx.compose.ui.tooling.preview.Preview //welight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.rohkee.audio_test.ui.theme.WeLightTheme //welight
import be.tarsos.dsp.AudioDispatcher //TarsosDSP 라이브러리를 활용하여 오디오 처리를 수행하는 데 필요한 클래스
import be.tarsos.dsp.AudioEvent //TarsosDSP 라이브러리를 활용하여 오디오 처리를 수행하는 데 필요한 클래스
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.PitchProcessor
import be.tarsos.dsp.onsets.OnsetHandler
import be.tarsos.dsp.onsets.ComplexOnsetDetector
import be.tarsos.dsp.AudioProcessor
import java.io.File
import kotlin.math.roundToInt




class MainActivity : ComponentActivity() {
    private var dispatcher: AudioDispatcher? = null // 오디오를 실시간으로 처리하기 위해 사용되는 AudioDispatcher 인스턴스입니다.
//    private var wavFileWriter: WavFileWriter? = null //WAV 파일에 오디오 데이터를 저장하기 위한 클래스의 인스턴스
    private var currentPitch by mutableStateOf("0 Hz") //현재 탐지된 피치를 나타내며, UI에서 실시간으로 갱신
    private var currentTempo by mutableStateOf("0 BPM") //현재 탐지된 템포를 나타내며, UI에 실시간으로 표시

    private var lastBeatTime = 0L //마지막 박자 감지 시간을 저장하는 변수
    private var beatIntervals = mutableListOf<Long>() //박자 간격을 저장하는 리스트입니다. 이를 통해 평균 템포를 계산
    private val BEAT_HISTORY_SIZE = 4 //템포 계산에 사용할 최대 박자 기록 수입니다. 오래된 데이터를 제거하고 최근 값만 유지하는 데 사용


    // 권한 요청을 처리하기 위한 변수 - 오디오 권한이 부여되면 startAudioProcessing()을 호출해 오디오 처리를 시작
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startAudioProcessing()
        }
    }


//  onCreate 함수는 Activity가 생성될 때 호출, 여기서 초기 설정 및 UI 구성
    // savedInstanceState는 활동의 이전 상태 데이터를 담고 있는 Bundle 객체, 앱이 재시작될 때 이전 상태를 복원하는 데 사용
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // 상위 클래스의 onCreate 메서드를 호출하여 기본 Activity 설정을 실행
            // 이를 통해 안드로이드 시스템이 Activity를 올바르게 초기화하도록 보장
        enableEdgeToEdge() //Android 10 이상에서 화면을 전체 크기로 채우는 "엣지-투-엣지(edge-to-edge)" 모드를 활성화
                            // 상태바와 네비게이션 바의 색상과 투명도가 조정되어 더 매끄러운 화면 경험을 제공
        setContent { //Jetpack Compose의 기능,  Compose에서 UI를 설정하기 위한 기본 진입점
            WeLightTheme {
                //Scaffold는 기본적인 레이아웃 구조를 제공, 컨테이너 역할
                // Modifier.fillMaxSize() : Scaffold가 화면의 전체 크기를 채우도록 설정
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen( //MainScreen 컴포저블을 호출하여 currentPitch와 currentTempo를 전달하고, 버튼 클릭 시 호출될 함수들도 설정
                        pitch = currentPitch, // 현재 탐지된 피치 값을 전달
                        tempo = currentTempo, // 현재 탐지된 템포 값을 전달
                        onStartClick = { checkAndRequestAudioPermission() }, // 오디오 권한 확인 및 오디오 처리 시작
                        onStopClick = { stopAudioProcessing() }, // 오디오 처리 중지
                        onCopyClick = { copyToExternalStorage() }, // 녹음 파일 외부 저장소로 복사
                        modifier = Modifier.padding(innerPadding) // 내부 여백을 설정하여 UI 요소가 겹치지 않도록 조정
                    )
                }
            }
        }
    }// end of onCreate


    //오디오 녹음과 외부 저장소 쓰기 권한이 있는지 확인
    private fun checkAndRequestAudioPermission() {
        // 권한이 이미 있으면 startAudioProcessing()을 호출하여 오디오 처리를 시작

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                startAudioProcessing()
            }
            // 권한이 없다면 requestPermissionLauncher를 사용해 권한을 요청
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }


    // 마이크 입력을 사용하여 오디오를 실시간으로 분석하고 피치와 박자를 탐지한 후 WAV 파일에 저장
    private fun startAudioProcessing() {
        // AudioDispatcher 객체를 초기화
        // fromDefaultMicrophone 메서드는 기본 마이크로부터 22050Hz 샘플링 레이트, 1024 프레임 크기, 0 오버랩을 사용하여 오디오 데이터를 가져옴
        // 이 객체는 오디오 데이터를 실시간으로 가져와 분석을 수행합니다.
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)

        // 외부 저장소 파일 경로 설정 및 WavFileWriter 초기화
        // 생성된 파일(externalFile)에 오디오 데이터를 쓰는 역할을 하는 객체로, 22050Hz 샘플링 레이트와 1 채널(모노)로 초기화
        val externalFile = File(getExternalFilesDir(null), "recording.wav")
        println("Saving file to: ${externalFile.absolutePath}")
        wavFileWriter = WavFileWriter(externalFile, 22050, 1)


        // 피치(음 높이)를 탐지하기 위한 핸들러
        val pdh = PitchDetectionHandler { result: PitchDetectionResult, _: AudioEvent ->
            val pitchInHz = result.pitch //탐지된 피치를 Hz 단위로 제공
            runOnUiThread { //processPitch 함수로 전달하여 UI에서 실시간으로 갱신
                processPitch(pitchInHz)
            }
        }
        val pitchProcessor = PitchProcessor( //피치 분석을 수행하는 객체
            PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, //FFT_YIN 알고리즘을 사용해 피치를 탐지
            22050f, //샘플링 레이트는 22050Hz
            1024, //프레임 크기는 1024
            pdh //피치 탐지 결과 처리 핸들러
        )


        // onsetHandler: 박자(템포)를 탐지하기 위한 핸들러
        //Largo (40-60 BPM) ~ Prestissimo (200 BPM 이상) -> 1박자 0.3초 (300ms)
        val onsetHandler = OnsetHandler { _: Double, _: Double ->
            val currentTime = System.currentTimeMillis() //현재 시간을 밀리초 단위로 기록
            if (lastBeatTime > 0) {
                val interval = currentTime - lastBeatTime //현재 박자와 이전 박자 간의 시간 차이로,
                // 이를 beatIntervals 리스트에 저장하여 일정한 간격을 유지

                if (interval > 100) {
                    beatIntervals.add(interval)
                    if (beatIntervals.size > BEAT_HISTORY_SIZE) {
                        beatIntervals.removeAt(0)
                    }
                    calculateTempo()
                }
            }
            lastBeatTime = currentTime
        }


        // 오디오 신호에서 박자 변화를 탐지하는 객체, 프레임 크기는 1024
        val onsetDetector = ComplexOnsetDetector(1024).apply {
            setThreshold(0.2) //감지민감도 0.2
            setHandler(onsetHandler) //박자 탐지 결과를 onsetHandler로 전달
        }

        // ispatcher에 pitchProcessor와 onsetDetector를 추가
        //실시간으로 피치와 박자를 동시에 분석하도록 설정
        dispatcher?.addAudioProcessor(pitchProcessor)
        dispatcher?.addAudioProcessor(onsetDetector)

        //객체를 추가하여 오디오 데이터를 WAV 파일에 저장
        dispatcher?.addAudioProcessor(object : AudioProcessor {
            // process 함수는 audioEvent.byteBuffer에서 오디오 데이터를 가져와 wavFileWriter를 통해 파일에 기록
            override fun process(audioEvent: AudioEvent): Boolean {
                val buffer = audioEvent.byteBuffer
                wavFileWriter?.writeData(buffer)
                return true
            }

            // 오디오 처리가 종료될 때 호출되며, 현재는 아무 작업도 수행하지 않습니다.
            override fun processingFinished() {
            }
        })

        //dispatcher를 새 스레드에서 실행하여 오디오 처리가 UI 스레드와 별도로 실행되도록 합니다.
        //이렇게 하면 오디오 처리가 UI를 방해하지 않고 백그라운드에서 계속 수행됨 <-왜???
        Thread(dispatcher, "Audio Thread").start()

    }// end of startAudioProcessing


    private fun calculateTempo() {
//        if (beatIntervals.size >= 2) {
        if (beatIntervals.size >= 4) {
            val averageInterval = beatIntervals.average()
            val bpm = (60000.0 / averageInterval).roundToInt()

            if (bpm in 40..220) {
                runOnUiThread {
                    currentTempo = "$bpm BPM" //변수에 템포를 문자열 형태로 저장하며, "BPM" 단위를 추가하여 UI에 표시
                }
            }
        }
    }

    //오디오 처리를 중지하고 리소스를 해제하며, 필요한 데이터를 초기화하는 역할
    private fun stopAudioProcessing() {
        dispatcher?.stop() //dispatcher는 오디오 처리를 담당하는 객체.stop() ( null이 아닐 때만)
        dispatcher = null
        wavFileWriter?.close() // 파일 쓰기 완료
        wavFileWriter = null
        beatIntervals.clear() //리스트를 초기화
        lastBeatTime = 0
        currentTempo = "0 BPM"

        // 파일 크기 확인
        val externalFile = File(getExternalFilesDir(null), "recording.wav")
        println("Recorded file size: ${externalFile.length()} bytes")
        copyToExternalStorage()
    }



} // end of ComponentActivity

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeLightTheme {
        Greeting("Android")
    }
}