package com.chrisp.healthdetect.model

data class FraminghamRequest(
    val name: String,
    val age: Int,
    val gender: String,
    val race: String?,
    val totalCholesterol: Int,
    val hdlCholesterol: Int,
    val systolicBP: Int,
    val isSmoker: Boolean,
    val isDiabetic: Boolean,
    val restingHeartRates: List<Int>
)

data class FraminghamResponse(
    val user: User,
    val framingham: FraminghamResult,
    val ascvd: AscvdResult
)

data class User(
    val id: String,
    val name: String,
    val age: Int,
    val gender: String,
    val race: String?
)

data class FraminghamResult(
    val riskScore: Float,
    val riskLevel: String,
    val riskPercentage: String,
    val avgHeartRate: Float?,
    val message: String,
    val assessmentDate: String
)

data class AscvdResult(
    val ascvdScore: Float,
    val ascvdLevel: String,
    val ascvdMessage: String,
    val assessmentDate: String
)

data class NutritionRequest(
    val userId: String,
    val weight: Float,
    val height: Float,
    val activityLevel: String,
    val stressLevel: String
)

data class NutritionResponse(
    val user: User,
    val nutritionData: NutritionData,
    val result: NutritionResult
)

data class NutritionData(
    val id: String,
    val userId: String,
    val weight: Float,
    val height: Float,
    val activityLevel: String,
    val stressLevel: String
)

data class NutritionResult(
    val bmi: Float,
    val bmiCategory: String,
    val idealWeight: Float,
    val bmr: Float,
    val tee: Float,
    val proteinGram: Float,
    val proteinKcal: Float,
    val proteinPercent: Float,
    val fatGram: Float,
    val fatKcal: Float,
    val fatPercent: Float,
    val carbGram: Float,
    val carbKcal: Float,
    val carbPercent: Float
)