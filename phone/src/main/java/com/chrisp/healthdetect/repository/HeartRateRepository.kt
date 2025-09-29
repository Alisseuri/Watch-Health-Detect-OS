package com.chrisp.healthdetect.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


object HeartRateRepository {

    private val _heartRateDataList = MutableStateFlow<List<Int>>(emptyList())
    val heartRateDataList = _heartRateDataList.asStateFlow()

    fun addHeartRateData(heartRate: Int) {
        if (heartRate > 0) {
            val updatedList = _heartRateDataList.value + heartRate
            _heartRateDataList.value = updatedList
        }
    }

    fun clearData() {
        _heartRateDataList.value = emptyList()
    }
}