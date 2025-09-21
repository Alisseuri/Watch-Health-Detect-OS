package com.chrisp.healthdetect.ui.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisp.healthdetect.model.*
import com.chrisp.healthdetect.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period

// === ENUMs ===
enum class Gender { PRIA, WANITA }

enum class YesNo { TIDAK, YA }

enum class Race(val displayName: String) {
    PUTIH("Kulit Putih"),
    AFRIKA_AMERIKA("Afrika-Amerika"),
    ASIA("Asia"),
    LAINNYA("Lainnya")
}

enum class ActivityLevel(val displayName: String, val value: Float) {
    BEDREST("BEDREST", 1.2f),
    RINGAN("RINGAN", 1.3f),
    SEDANG("SEDANG", 1.4f),
    BERAT("BERAT", 1.5f)
}

enum class StressLevel(val displayName: String, val value: Float) {
    RINGAN("RINGAN", 1.2f),
    SEDANG("SEDANG", 1.3f),
    BERAT("BERAT", 1.5f)
}

// === DATA CLASS ===
data class UserProfileData(
    val isEditMode : Boolean = true,
    val name: String = "",
    val dob: LocalDate? = null,
    val gender: Gender? = null,
    val race: Race? = null,
    val isSmoker: YesNo = YesNo.TIDAK,
    val hasDiabetes: YesNo = YesNo.TIDAK,
    val totalCholesterol: String = "",
    val hdlCholesterol: String = "",
    val systolicBp: String = "",
    val oxygenSaturation: String = "",
    val height: String = "",
    val weight: String = "",
    val activityLevel: ActivityLevel = ActivityLevel.BEDREST,
    val stressLevel: StressLevel = StressLevel.BERAT
)

// === VIEWMODEL ===
class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    var uiState by mutableStateOf(UserProfileData())
        private set

    private val _framinghamResult = MutableStateFlow<FraminghamResponse?>(null)
    val framinghamResult: StateFlow<FraminghamResponse?> = _framinghamResult

    private val _ascvdResult = MutableStateFlow<FraminghamResponse?>(null)
    val ascvdResult: StateFlow<FraminghamResponse?> = _ascvdResult

    private val _nutritionResult = MutableStateFlow<NutritionResponse?>(null)
    val nutritionResult: StateFlow<NutritionResponse?> = _nutritionResult

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // === Setter Functions ===
    fun onNameChange(newName: String) { uiState = uiState.copy(name = newName) }
    fun onDobChange(newDob: LocalDate) { uiState = uiState.copy(dob = newDob) }
    fun onGenderChange(newGender: Gender) { uiState = uiState.copy(gender = newGender) }
    fun onRaceChange(newRace: Race) { uiState = uiState.copy(race = newRace) }
    fun onSmokerChange(selection: YesNo) { uiState = uiState.copy(isSmoker = selection) }
    fun onDiabetesChange(selection: YesNo) { uiState = uiState.copy(hasDiabetes = selection) }
    fun onTotalCholesterolChange(value: String) { uiState = uiState.copy(totalCholesterol = value) }
    fun onHdlCholesterolChange(value: String) { uiState = uiState.copy(hdlCholesterol = value) }
    fun onSystolicBpChange(value: String) { uiState = uiState.copy(systolicBp = value) }
    fun onOxygenSaturationChange(value: String) { uiState = uiState.copy(oxygenSaturation = value) }
    fun onHeightChange(value: String) { uiState = uiState.copy(height = value) }
    fun onWeightChange(value: String) { uiState = uiState.copy(weight = value) }
    fun onActivityLevelChange(newLevel: ActivityLevel) { uiState = uiState.copy(activityLevel = newLevel) }
    fun onStressLevelChange(newLevel: StressLevel) { uiState = uiState.copy(stressLevel = newLevel) }
    fun setEditMode(isEditing: Boolean) { uiState = uiState.copy(isEditMode = isEditing) }
    fun saveProfile() { setEditMode(false) }

    // === Validation ===
    fun isTotalCholesterolNormal(): Boolean? = uiState.totalCholesterol.toIntOrNull()?.let { it < 200 }
    fun isHdlCholesterolNormal(): Boolean? = uiState.hdlCholesterol.toIntOrNull()?.let { it >= 40 }
    fun isSystolicBpNormal(): Boolean? = uiState.systolicBp.toIntOrNull()?.let { it < 140 }
    fun isOxygenSaturationNormal(): Boolean? = uiState.oxygenSaturation.toIntOrNull()?.let { it in 95..100 }

    // === Age Calculation ===
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAge(): Int? = uiState.dob?.let {
        Period.between(it, LocalDate.now()).years
    }

    // === Validation before submit ===
    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateInput(): String? {
        if (uiState.name.isBlank()) return "Nama harus diisi"
        if (uiState.dob == null) return "Tanggal lahir harus diisi"
        if (uiState.gender == null) return "Jenis kelamin harus dipilih"
        if (uiState.race == null) return "Ras harus dipilih"
        if (uiState.totalCholesterol.isBlank()) return "Kolesterol total harus diisi"
        if (uiState.hdlCholesterol.isBlank()) return "HDL kolesterol harus diisi"
        if (uiState.systolicBp.isBlank()) return "Tekanan darah sistolik harus diisi"
        if (uiState.height.isBlank()) return "Tinggi badan harus diisi"
        if (uiState.weight.isBlank()) return "Berat badan harus diisi"

        // Validate numeric fields
        if (uiState.totalCholesterol.toIntOrNull() == null) return "Kolesterol total harus berupa angka"
        if (uiState.hdlCholesterol.toIntOrNull() == null) return "HDL kolesterol harus berupa angka"
        if (uiState.systolicBp.toIntOrNull() == null) return "Tekanan darah sistolik harus berupa angka"
        if (uiState.height.toFloatOrNull() == null) return "Tinggi badan harus berupa angka"
        if (uiState.weight.toFloatOrNull() == null) return "Berat badan harus berupa angka"

        return null
    }

    // === Submit to Repository ===
    @RequiresApi(Build.VERSION_CODES.O)
    fun submitAll() {
        val validationError = validateInput()
        if (validationError != null) {
            _error.value = validationError
            return
        }

        val age = getAge()!!
        val genderStr = if (uiState.gender == Gender.PRIA) "male" else "female"
        val raceStr = uiState.race!!.displayName

        val framinghamRequest = FraminghamRequest(
            name = uiState.name,
            age = age,
            gender = genderStr,
            race = raceStr,
            totalCholesterol = uiState.totalCholesterol.toInt(),
            hdlCholesterol = uiState.hdlCholesterol.toInt(),
            systolicBP = uiState.systolicBp.toInt(),
            isSmoker = uiState.isSmoker == YesNo.YA,
            isDiabetic = uiState.hasDiabetes == YesNo.YA,
            restingHeartRates = listOf(87)
        )

        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                android.util.Log.d("ProfileViewModel", "Starting submitAll process...")

                // Single call to Framingham (which includes ASCVD data)
                val framinghamRes = repository.submitFramingham(framinghamRequest)
                android.util.Log.d("ProfileViewModel", "Framingham response received: userId = ${framinghamRes.user.id}")

                // IMPORTANT: Update state flows immediately after getting response
                _framinghamResult.value = framinghamRes
                _ascvdResult.value = framinghamRes  // Same response contains ASCVD data

                // Nutrition call
                val userId = framinghamRes.user.id
                if (userId.isNullOrBlank()) {
                    _error.value = "User ID not found from Framingham response"
                    android.util.Log.e("ProfileViewModel", "User ID is null or blank")
                    return@launch
                }

                android.util.Log.d("ProfileViewModel", "Making nutrition call with userId: $userId")
                val nutritionReq = NutritionRequest(
                    userId = userId,
                    weight = uiState.weight.toFloat(),
                    height = uiState.height.toFloat(),
                    activityLevel = uiState.activityLevel.name.lowercase(),
                    stressLevel = uiState.stressLevel.name.lowercase()
                )
                val nutritionRes = repository.submitNutrition(nutritionReq)
                android.util.Log.d("ProfileViewModel", "Nutrition response received: userId = ${nutritionRes.user.id}")

                // IMPORTANT: Update nutrition result state flow
                _nutritionResult.value = nutritionRes

                android.util.Log.d("ProfileViewModel", "All data submitted successfully")
                android.util.Log.d("ProfileViewModel", "FraminghamResult set: ${_framinghamResult.value != null}")
                android.util.Log.d("ProfileViewModel", "NutritionResult set: ${_nutritionResult.value != null}")

                setEditMode(false)
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                android.util.Log.e("ProfileViewModel", "Submit failed", e)
            } finally {
                _loading.value = false
            }
        }
    }

    // Clear error when user starts editing again
    fun clearError() {
        _error.value = null
    }

    // === Get nutrition data from API ===
    fun getNutritionResult(userId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                android.util.Log.d("ProfileViewModel", "Calling getNutritionResult for userId: $userId")
                val nutritionRes = repository.getNutritionResult(userId)
                android.util.Log.d("ProfileViewModel", "Got nutrition response: ${nutritionRes.user.id}")
                _nutritionResult.value = nutritionRes
                android.util.Log.d("ProfileViewModel", "Nutrition result set successfully")
            } catch (e: Exception) {
                _error.value = "Error loading nutrition data: ${e.message}"
                android.util.Log.e("ProfileViewModel", "Failed to get nutrition result", e)
            } finally {
                _loading.value = false
            }
        }
    }

    // === Check if user data exists ===
    fun hasCompletedProfile(): Boolean {
        // Check if we have either framingham or nutrition result with user data
        val hasFramingham = _framinghamResult.value?.user?.id != null
        val hasNutrition = _nutritionResult.value?.user?.id != null
        android.util.Log.d("ProfileViewModel", "hasFramingham: $hasFramingham, hasNutrition: $hasNutrition")
        return hasFramingham || hasNutrition
    }

    fun getCurrentUserId(): String? {
        // Try to get user ID from either framingham or nutrition result
        val framinghamId = _framinghamResult.value?.user?.id
        val nutritionId = _nutritionResult.value?.user?.id
        val userId = framinghamId ?: nutritionId
        android.util.Log.d("ProfileViewModel", "getCurrentUserId - framinghamId: $framinghamId, nutritionId: $nutritionId, returning: $userId")
        return userId
    }
}