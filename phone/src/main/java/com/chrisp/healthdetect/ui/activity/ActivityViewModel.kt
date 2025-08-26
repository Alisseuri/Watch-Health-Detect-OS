package com.chrisp.healthdetect.ui.activity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.random.Random

data class DailySummary(
    val date: LocalDate,
    val caloriesBurned: Int,
    val avgHeartRate: Int,
    val restingHeartRate: Int,
    val steps: Float
)

@RequiresApi(Build.VERSION_CODES.O)
data class ActivityUiState (
    val weeklyData: List<DailySummary> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val chartMaxValue: Float = 4000f,
    val yAxisLabels: List<String> = listOf("3k", "2k", "1k", "0")
) {
    val selectedDaySummary: DailySummary?
        get() = weeklyData.find { it.date == selectedDate }
}

@RequiresApi(Build.VERSION_CODES.O)
class ActivityViewModel : ViewModel() {
    var uiState by mutableStateOf(ActivityUiState())
        private set

    init {
        loadWeeklyData(LocalDate.now())
    }

    // TEST

    fun loadWeeklyData(dateInWeek: LocalDate) {
        val startOfWeek = dateInWeek.with(DayOfWeek.MONDAY)
        val dummyData = (0..6).map { dayIndex ->
            val date = startOfWeek.plusDays(dayIndex.toLong())
            DailySummary(
                date = date,
                caloriesBurned = Random.nextInt(100, 500),
                avgHeartRate = Random.nextInt(80, 120),
                restingHeartRate = Random.nextInt(50, 70),
                steps = Random.nextInt(500, 3500).toFloat()
            )
        }

        uiState = uiState.copy(
            weeklyData = dummyData,
            selectedDate = dateInWeek
        )
    }

    fun onDaySelected(dayIndex: Int) {
        if (dayIndex in uiState.weeklyData.indices) {
            uiState = uiState.copy(selectedDate = uiState.weeklyData[dayIndex].date)
        }
    }

    fun onMonthYearChanged(date: LocalDate) {
        loadWeeklyData(date)
    }
}

private fun calculateYAxisValues(data: List<DailySummary>): Pair<Float, List<String>> {
    val maxSteps = data.maxOfOrNull { it.steps } ?: 0f

    if (maxSteps == 0f) {
        return 4000f to listOf("3k", "2k", "1k", "0")
    }

    val dynamicMaxValue = ceil(maxSteps / 1000f) * 1000f

    val labels = List(4) { i ->
        val value = dynamicMaxValue * ( (3 - i) / 3f)
        when {
            value >= 1000 -> "${(value / 1000).toInt()}k"
            else -> value.toInt().toString()
        }
    }

    return dynamicMaxValue to labels
}