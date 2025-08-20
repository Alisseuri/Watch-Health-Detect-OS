package com.chrisp.healthdetect.viewmodel

import androidx.lifecycle.*
import com.chrisp.healthdetect.model.FraminghamRequest
import com.chrisp.healthdetect.model.FraminghamResponse
import com.chrisp.healthdetect.repository.FraminghamRepository
import kotlinx.coroutines.launch

class FraminghamViewModel(private val repository: FraminghamRepository) : ViewModel() {
    private val _result = MutableLiveData<FraminghamResponse?>()
    val result: LiveData<FraminghamResponse?> = _result

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun submitFramingham(request: FraminghamRequest) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = repository.submitFramingham(request)
                _result.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}