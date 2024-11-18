package com.rohkee.audio_test

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.onsets.ComplexOnsetDetector
import be.tarsos.dsp.onsets.OnsetHandler
import kotlin.math.roundToInt

class TempoDetector {
    private var dispatcher: AudioDispatcher? = null
    private var lastBeatTime = 0L
    private var beatIntervals = mutableListOf<Long>()
    private val BEAT_HISTORY_SIZE = 4

    // Configuration
    private val sampleRate = 22050
    private val bufferSize = 1024
    private val onsetThreshold = 0.1

    // Callbacks
    private var onTempoUpdate: ((String) -> Unit)? = null
    private var onBeatDetected: (() -> Unit)? = null

    fun startDetection(
        onTempoUpdate: (String) -> Unit,
        onBeatDetected: (() -> Unit)? = null
    ) {
        this.onTempoUpdate = onTempoUpdate
        this.onBeatDetected = onBeatDetected

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, 0)

        val onsetHandler = OnsetHandler { _: Double, _: Double ->
            val currentTime = System.currentTimeMillis()
            onBeatDetected?.invoke()

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

        val onsetDetector = ComplexOnsetDetector(bufferSize).apply {
            setThreshold(onsetThreshold)
            setHandler(onsetHandler)
        }

        dispatcher?.addAudioProcessor(onsetDetector)
        Thread(dispatcher, "Audio Thread").start()
    }

    private fun calculateTempo() {
        if (beatIntervals.size >= 4) {
            val mean = beatIntervals.average()
            val standardDeviation = beatIntervals.map { (it - mean) * (it - mean) }.average().let { Math.sqrt(it) }

            val filteredIntervals = beatIntervals.filter { interval ->
                Math.abs(interval - mean) <= 2 * standardDeviation
            }

            val averageInterval = filteredIntervals.average()
            val bpm = (60000.0 / averageInterval).roundToInt()

            if (bpm in 40..220) {
                onTempoUpdate?.invoke("$bpm BPM")
            }
        }
    }

    fun stopDetection() {
        dispatcher?.stop()
        dispatcher = null
        beatIntervals.clear()
        lastBeatTime = 0
        onTempoUpdate = null
        onBeatDetected = null
    }
}