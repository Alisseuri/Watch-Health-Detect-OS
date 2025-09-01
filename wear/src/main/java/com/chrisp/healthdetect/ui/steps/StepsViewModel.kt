package com.chrisp.healthdetect.ui.steps

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisp.healthdetect.ui.heartrate.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class StepsResult(
    val totalSteps: Int,
    val durationSeconds: Long,
    val finishedAt: LocalDateTime
)

class StepsViewModel : ViewModel() {

    var timerState by mutableStateOf(TimerState.STOPPED)
        private set

    var stepCount by mutableStateOf(0)
        private set

    var elapsedTime by mutableStateOf(0L)
        private set

    var result by mutableStateOf<StepsResult?>(null)
        private set

    private var timerJob: Job? = null

    fun startTimer() {
        if (timerState == TimerState.RUNNING) return

        if (timerState == TimerState.STOPPED || timerState == TimerState.FINISHED) {
            elapsedTime = 0L
            stepCount = 0 // Reset langkah
            result = null
        }

        timerState = TimerState.RUNNING
        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis() - (elapsedTime * 1000)
            while (timerState == TimerState.RUNNING) {
                elapsedTime = (System.currentTimeMillis() - startTime) / 1000
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
        // Buat hasil akhir
        result = StepsResult(
            totalSteps = stepCount,
            durationSeconds = elapsedTime,
            finishedAt = LocalDateTime.now()
        )
        timerState = TimerState.FINISHED
        finishSession()
    }

    fun finishSession() {
        timerState = TimerState.STOPPED
        elapsedTime = 0L
        stepCount = 0
        result = null
    }

    fun updateStepCount(newCount: Int) {
        stepCount = newCount
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