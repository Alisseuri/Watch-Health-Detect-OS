package com.chrisp.healthdetect.ui.heartrate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class TimerState {
    STOPPED,
    RUNNING,
    PAUSED,
    FINISHED
}

data class HeartRateResult(
    val averageBpm: Int,
    val durationSeconds: Long,
    val finishedAt: LocalDateTime
)


class HeartRateViewModel : ViewModel() {

    var timerState by mutableStateOf(TimerState.STOPPED)
        private set

    var heartRate by mutableStateOf(0)
        private set

    var elapsedTime by mutableStateOf(0L)
        private set

    var result by mutableStateOf<HeartRateResult?>(null)
        private set

    private val heartRateSamples = mutableStateListOf<Int>()
    private var timerJob: Job? = null

    fun startTimer() {
        if (timerState == TimerState.RUNNING) return

        if (timerState == TimerState.STOPPED || timerState == TimerState.FINISHED) {
            elapsedTime = 0L
            heartRateSamples.clear()
            result = null
        }

        timerState = TimerState.RUNNING
        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis() - (elapsedTime * 1000)
            while (timerState == TimerState.RUNNING) {
                elapsedTime = (System.currentTimeMillis() - startTime) / 1000
                if (heartRate > 0) heartRateSamples.add(heartRate)
                delay(1000L)
            }
        }
    }

    fun pauseTimer() {
        timerState = TimerState.PAUSED
        timerJob?.cancel()
    }

    fun stopTimer() {
        timerJob?.cancel()

        val averageBpm = if (heartRateSamples.isNotEmpty()) heartRateSamples.average().toInt() else 0
        result = HeartRateResult(
            averageBpm = averageBpm,
            durationSeconds = elapsedTime,
            finishedAt = LocalDateTime.now()
        )

        timerState = TimerState.FINISHED
    }

    fun finishSession() {
        timerState = TimerState.STOPPED
        elapsedTime = 0L
        heartRateSamples.clear()
        result = null
    }

    fun updateHeartRate(newRate: Int) {
        heartRate = newRate
    }

    fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    fun formatDuration(seconds: Long): String {
        val hr = seconds / 3600
        val min = (seconds % 3600) / 60
        val sec = seconds % 60
        return "${hr} hr ${min} mins ${sec} secs"
    }

    fun formatFinishTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy | HH.mm", Locale.getDefault())
        return dateTime.format(formatter)
    }

}