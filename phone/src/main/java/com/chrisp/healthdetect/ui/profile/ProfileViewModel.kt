package com.chrisp.healthdetect.ui.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.Period

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

class ProfileViewModel : ViewModel() {
    var uiState by mutableStateOf(UserProfileData())
        private set

    fun onNameChange(newName: String) {
        uiState = uiState.copy(name = newName)
    }

    fun onDobChange(newDob: LocalDate) {
        uiState = uiState.copy(dob = newDob)
    }

    fun onGenderChange(newGender: Gender) {
        uiState = uiState.copy(gender = newGender)
    }

    fun onRaceChange(newRace: Race) {
        uiState = uiState.copy(race = newRace)
    }

    fun onSmokerChange(selection: YesNo) {
        uiState = uiState.copy(isSmoker = selection)
    }

    fun onDiabetesChange(selection: YesNo) {
        uiState = uiState.copy(hasDiabetes = selection)
    }

    fun onTotalCholesterolChange(value: String) {
        uiState = uiState.copy(totalCholesterol = value)
    }

    fun onHdlCholesterolChange(value: String) {
        uiState = uiState.copy(hdlCholesterol = value)
    }

    fun onActivityLevelChange(newLevel: ActivityLevel) {
        uiState = uiState.copy(activityLevel = newLevel)
    }

    fun onSystolicBpChange(value: String) {
        uiState = uiState.copy(systolicBp = value)
    }

    fun onOxygenSaturationChange(value: String) {
        uiState = uiState.copy(oxygenSaturation = value)
    }

    fun onHeightChange(value: String) {
        uiState = uiState.copy(height = value)
    }

    fun onWeightChange(value: String) {
        uiState = uiState.copy(weight = value)
    }

    fun onStressLevelChange(newLevel: StressLevel) {
        uiState = uiState.copy(stressLevel = newLevel)
    }

    fun setEditMode(isEditing: Boolean) {
        uiState = uiState.copy(isEditMode = isEditing)
    }

    fun saveProfile() {
        setEditMode(false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAge(): Int? = uiState.dob?.let {
        Period.between(it, LocalDate.now()).years
    }

    fun isTotalCholesterolNormal(): Boolean? {
        val value = uiState.totalCholesterol.toIntOrNull() ?: return null
        return value < 200
    }

    fun isHdlCholesterolNormal(): Boolean? {
        val value = uiState.hdlCholesterol.toIntOrNull() ?: return null
        return value >= 40
    }

    fun isSystolicBpNormal(): Boolean? {
        val value = uiState.systolicBp.toIntOrNull() ?: return null
        return value < 140
    }

    fun isOxygenSaturationNormal(): Boolean? {
        val value = uiState.oxygenSaturation.toIntOrNull() ?: return null
        return value in 95..100
    }
}