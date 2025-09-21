package com.chrisp.healthdetect

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Singleton object to hold sensor data
object SensorDataRepository {

    // Heart Rate Data
    private val _heartRate = MutableStateFlow(0)
    val heartRate = _heartRate.asStateFlow()

    fun updateHeartRate(newRate: Int) {
        _heartRate.value = newRate
    }

    // Step Count Data
    // This will hold the raw step count from the sensor
    private val _stepCount = MutableStateFlow(0)
    val stepCount = _stepCount.asStateFlow()

    fun updateStepCount(newCount: Int) {
        _stepCount.value = newCount
    }
}