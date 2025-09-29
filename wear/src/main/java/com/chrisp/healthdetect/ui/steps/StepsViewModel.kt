package com.chrisp.healthdetect.ui.steps

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisp.healthdetect.HeartRateService
import com.chrisp.healthdetect.ui.heartrate.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.chrisp.healthdetect.SensorDataRepository

data class StepsResult(
    val caloriesBurned: Int,
    val totalSteps: Int,
    val averageBpm: Int,
    val durationSeconds: Long,
    val finishedAt: LocalDateTime
)

class StepsViewModel : ViewModel() {

    var timerState by mutableStateOf(TimerState.STOPPED)
        private set
    var elapsedTime by mutableStateOf(0L)
        private set
    var heartRate by mutableStateOf(0)
        private set
    var stepCount by mutableStateOf(0)
        private set
    var result by mutableStateOf<StepsResult?>(null)
        private set

    private val heartRateSamples = mutableStateListOf<Int>()
    private var timerJob: Job? = null
    private var initialStepCount = -1

    init {
        viewModelScope.launch {
            SensorDataRepository.heartRate.collect { newRate ->
                heartRate = newRate
            }
        }
        viewModelScope.launch {
            SensorDataRepository.stepCount.collect { newCount ->
                if (timerState == TimerState.RUNNING || timerState == TimerState.PAUSED) {
                    if (initialStepCount < 0) {
                        initialStepCount = newCount
                    }
                    stepCount = newCount - initialStepCount
                }
            }
        }
    }

    fun startTimer(context: Context) {
        if (timerState == TimerState.RUNNING) return

        val intent = Intent(context, HeartRateService::class.java).apply {
            action = "ACTION_START_EXERCISE"
        }
        context.startService(intent)

        if (timerState == TimerState.STOPPED || timerState == TimerState.FINISHED) {
            resetSession()
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

    fun stopTimer(context: Context) {
        timerJob?.cancel()

        val intent = Intent(context, HeartRateService::class.java).apply {
            action = "ACTION_STOP_EXERCISE"
        }
        context.startService(intent)

        val avgBpm = if (heartRateSamples.isNotEmpty()) heartRateSamples.average().toInt() else 0
        val calories = (stepCount * 0.04f).toInt() + ((avgBpm - 60).coerceAtLeast(0) * (elapsedTime / 60f) * 0.1f).toInt()

        result = StepsResult(
            caloriesBurned = calories,
            totalSteps = stepCount,
            averageBpm = avgBpm,
            durationSeconds = elapsedTime,
            finishedAt = LocalDateTime.now()
        )
        timerState = TimerState.FINISHED
    }

    private fun resetSession() {
        elapsedTime = 0L
        heartRate = 0
        stepCount = 0
        heartRateSamples.clear()
        result = null
        initialStepCount = -1
    }

    fun finishSessionAndReset() {
        resetSession()
        timerState = TimerState.STOPPED
    }

    fun formatTime(seconds: Long): String {
        val hours = seconds / 3600; val minutes = (seconds % 3600) / 60; val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
    fun formatDuration(seconds: Long): String {
        val hr = seconds / 3600; val min = (seconds % 3600) / 60; val sec = seconds % 60
        return "${hr} hr ${min} mins ${sec} secs"
    }
    fun formatFinishTime(dateTime: LocalDateTime): String {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy | HH.mm", Locale.getDefault()))
    }

}