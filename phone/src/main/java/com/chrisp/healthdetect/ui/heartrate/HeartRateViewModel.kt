package com.chrisp.healthdetect.ui.heartrate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisp.healthdetect.repository.HeartRateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HeartRateDetailViewModel : ViewModel() {
    private val heartRateData = HeartRateRepository.heartRateDataList

    val averageHeartRate = heartRateData.map { list ->
        if (list.isNotEmpty()) list.average().toInt() else 0
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    val minHeartRate = heartRateData.map { list ->
        list.minOrNull() ?: 0
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    val maxHeartRate = heartRateData.map { list ->
        list.maxOrNull() ?: 0
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    override fun onCleared() {
        super.onCleared()
        HeartRateRepository.clearData()
    }
}