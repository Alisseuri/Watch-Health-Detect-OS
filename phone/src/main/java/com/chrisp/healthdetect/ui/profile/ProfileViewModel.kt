package com.chrisp.healthdetect.ui.profile

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisp.healthdetect.model.*
import com.chrisp.healthdetect.repository.HeartRateRepository
import com.chrisp.healthdetect.repository.ProfileRepository
import com.chrisp.healthdetect.ui.util.ageToDobString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

data class UserDisplay(
    val id: String,
    val name: String,
    val dobString: String
)

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    var uiState by mutableStateOf(UserProfileData())
        private set

    var isSearchDialogVisible by mutableStateOf(false)
        private set
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())

    var searchQuery by mutableStateOf("")
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    val allUsersForDisplay: StateFlow<List<UserDisplay>> = _allUsers.map { userList ->
        userList.map { user ->
            UserDisplay(
                id = user.id,
                name = user.name,
                dobString = ageToDobString(user.age)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    val filteredUsers = derivedStateOf {
        // Gunakan allUsersForDisplay yang sudah bertipe List<UserDisplay>
        val usersToDisplay = allUsersForDisplay.value
        if (searchQuery.isBlank()) {
            usersToDisplay
        } else {
            usersToDisplay.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    val averageHeartRate: StateFlow<Int> = HeartRateRepository.heartRateDataList.map { list ->
        if (list.isNotEmpty()) list.average().toInt() else 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

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

    fun onSearchQueryChange(query: String) { searchQuery = query }

    fun showSearchDialog() {
        Log.d("ProfileViewModel", "showSearchDialog called. Attempting to load users.")
        loadAllUsers() // Memuat daftar pengguna saat dialog akan ditampilkan
        isSearchDialogVisible = true
    }

    fun hideSearchDialog() {
        isSearchDialogVisible = false
    }

    private fun loadAllUsers() {
        viewModelScope.launch {
            // Jangan tampilkan loading global, agar tidak mengganggu UI utama
            // _loading.value = true
            _error.value = null
            Log.d("ProfileViewModel", "loadAllUsers: Starting API call.")
            try {
                val usersFromApi = repository.getAllUsers()
                // PENTING: Log jumlah data yang berhasil didapat
                Log.d("ProfileViewModel", "loadAllUsers: Success! Fetched ${usersFromApi.size} users.")
                _allUsers.value = usersFromApi

            } catch (e: Exception) {
                // PENTING: Log jika terjadi error
                Log.e("ProfileViewModel", "loadAllUsers: FAILED to fetch users.", e)
                _error.value = "Gagal memuat daftar pengguna: ${e.message}"
            } finally {
                // _loading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectUser(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                // 1. Panggil API search dengan ID untuk mendapatkan data lengkap
                val searchResult = repository.searchUsers(userId)
                if (searchResult.isEmpty()) {
                    throw Exception("Pengguna dengan ID $userId tidak ditemukan.")
                }

                // Ambil data dari hasil pencarian pertama

                val userResult = searchResult.first()
                val nutritionData = userResult.nutritionData.firstOrNull()
                val healthData = userResult.healthData.firstOrNull()
                val riskAssessment = userResult.riskAssessments.firstOrNull()
                val userGender = if (userResult.gender.equals("male", ignoreCase = true)) {
                    Gender.PRIA
                } else {
                    Gender.WANITA
                }

                // 2. Isi uiState untuk ditampilkan di form input
                val calculatedDob = LocalDate.now().minusYears(userResult.age.toLong())

                uiState = uiState.copy(
                    isEditMode = false, // Langsung ke mode display
                    name = userResult.name,
                    dob = calculatedDob,
                    gender = userGender,
                    // Data kesehatan
                    race = Race.values().find { it.displayName == healthData?.race },
                    isSmoker = healthData?.isSmoker?.let { if (it) YesNo.YA else YesNo.TIDAK } ?: YesNo.TIDAK,
                    hasDiabetes = healthData?.isDiabetic?.let { if (it) YesNo.YA else YesNo.TIDAK } ?: YesNo.TIDAK,
                    totalCholesterol = healthData?.totalCholesterol?.toString() ?: "",
                    hdlCholesterol = healthData?.hdlCholesterol?.toString() ?: "",
                    systolicBp = healthData?.systolicBP?.toString() ?: "",
                    // Data Nutrisi
                    height = nutritionData?.height?.toString() ?: "",
                    weight = nutritionData?.weight?.toString() ?: "",
                    activityLevel = nutritionData?.activityLevel?.let { ActivityLevel.valueOf(it.uppercase()) } ?: ActivityLevel.BEDREST,
                    stressLevel = nutritionData?.stressLevel?.let { StressLevel.valueOf(it.uppercase()) } ?: StressLevel.RINGAN
                )

                // 3. Rekonstruksi dan isi StateFlow untuk halaman hasil (DisplayScreen)

                // Buat objek User
                val user = User(
                    id = userResult.id,
                    name = userResult.name,
                    age = userResult.age,
                    gender = userResult.gender,
                    race = healthData?.race
                )

                // Isi _nutritionResult
                if (nutritionData != null && nutritionData.result != null) {
                    _nutritionResult.value = NutritionResponse(
                        user = user,
                        nutritionData = com.chrisp.healthdetect.model.NutritionData(
                            id = nutritionData.id,
                            userId = nutritionData.userId,
                            weight = nutritionData.weight,
                            height = nutritionData.height,
                            activityLevel = nutritionData.activityLevel,
                            stressLevel = nutritionData.stressLevel
                        ),
                        result = NutritionResult(
                            bmi = nutritionData.result.bmi,
                            bmiCategory = nutritionData.result.bmiCategory,
                            idealWeight = nutritionData.result.idealWeight,
                            bmr = nutritionData.result.bmr,
                            tee = nutritionData.result.tee,
                            proteinGram = nutritionData.result.proteinGram,
                            proteinKcal = nutritionData.result.proteinKcal,
                            proteinPercent = nutritionData.result.proteinPercent,
                            fatGram = nutritionData.result.fatGram,
                            fatKcal = nutritionData.result.fatKcal,
                            fatPercent = nutritionData.result.fatPercent,
                            carbGram = nutritionData.result.carbGram,
                            carbKcal = nutritionData.result.carbKcal,
                            carbPercent = nutritionData.result.carbPercent
                        )
                    )
                }

                // Isi _framinghamResult dan _ascvdResult
                if (riskAssessment != null) {
                    val assessmentDate = riskAssessment.assessmentDate.let {
                        // Coba format tanggal ISO ke format yang lebih mudah dibaca
                        try {
                            val instant = Instant.parse(it)
                            val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                            localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                        } catch (e: Exception) {
                            it // fallback ke string asli jika format salah
                        }
                    }

                    val response = FraminghamResponse(
                        user = user,
                        framingham = FraminghamResult(
                            riskScore = riskAssessment.framinghamScore,
                            riskLevel = riskAssessment.framinghamLevel,
                            riskPercentage = riskAssessment.framinghamPercentage,
                            avgHeartRate = healthData?.avgHeartRate?.toFloat(),
                            message = riskAssessment.framinghamMessage,
                            assessmentDate = assessmentDate
                        ),
                        ascvd = AscvdResult(
                            ascvdScore = riskAssessment.ascvdScore,
                            ascvdLevel = riskAssessment.ascvdLevel,
                            ascvdMessage = riskAssessment.ascvdMessage,
                            assessmentDate = assessmentDate
                        )
                    )
                    _framinghamResult.value = response
                    _ascvdResult.value = response
                }

            } catch (e: Exception) {
                _error.value = "Gagal memuat data pengguna: ${e.message}"
                Log.e("ProfileViewModel", "Error selecting user", e)
            } finally {
                _loading.value = false
                hideSearchDialog()
            }
        }
    }

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