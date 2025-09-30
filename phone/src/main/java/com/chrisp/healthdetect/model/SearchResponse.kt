package com.chrisp.healthdetect.model

import com.google.gson.annotations.SerializedName

// Tipe data untuk respons utama, yaitu sebuah list dari UserSearchResult
typealias SearchResponse = List<UserSearchResult>

// Kelas utama yang merepresentasikan satu objek pengguna dalam hasil pencarian
data class UserSearchResult(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("nutritionData")
    val nutritionData: List<NutritionDataDetail>,

    @SerializedName("healthData")
    val healthData: List<HealthDataDetail>,

    @SerializedName("riskAssessments")
    val riskAssessments: List<RiskAssessmentDetail>
)

// Kelas untuk objek di dalam array "nutritionData"
data class NutritionDataDetail(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("weight")
    val weight: Float,

    @SerializedName("height")
    val height: Float,

    @SerializedName("activityLevel")
    val activityLevel: String,

    @SerializedName("stressLevel")
    val stressLevel: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("result")
    val result: NutritionResultDetail? // Bisa jadi null jika belum ada hasil
)

// Kelas untuk objek "result" di dalam "nutritionData"
// Dibuat baru karena field-nya sedikit berbeda dari NutritionResult yang ada
data class NutritionResultDetail(
    @SerializedName("id")
    val id: String,

    @SerializedName("nutritionDataId")
    val nutritionDataId: String,

    @SerializedName("bmi")
    val bmi: Float,

    @SerializedName("bmiCategory")
    val bmiCategory: String,

    @SerializedName("idealWeight")
    val idealWeight: Float,

    @SerializedName("bmr")
    val bmr: Float,

    @SerializedName("tee")
    val tee: Float,

    @SerializedName("proteinGram")
    val proteinGram: Float,

    @SerializedName("proteinKcal")
    val proteinKcal: Float,

    @SerializedName("proteinPercent")
    val proteinPercent: Float,

    @SerializedName("fatGram")
    val fatGram: Float,

    @SerializedName("fatKcal")
    val fatKcal: Float,

    @SerializedName("fatPercent")
    val fatPercent: Float,

    @SerializedName("carbGram")
    val carbGram: Float,

    @SerializedName("carbKcal")
    val carbKcal: Float,

    @SerializedName("carbPercent")
    val carbPercent: Float,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

// Kelas untuk objek di dalam array "healthData"
data class HealthDataDetail(
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("totalCholesterol")
    val totalCholesterol: Int,

    @SerializedName("hdlCholesterol")
    val hdlCholesterol: Int,

    @SerializedName("systolicBP")
    val systolicBP: Int,

    @SerializedName("isSmoker")
    val isSmoker: Boolean,

    @SerializedName("isDiabetic")
    val isDiabetic: Boolean,

    @SerializedName("avgHeartRate")
    val avgHeartRate: Int?,

    @SerializedName("race")
    val race: String?,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

// Kelas untuk objek di dalam array "riskAssessments"
data class RiskAssessmentDetail(
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("healthDataId")
    val healthDataId: String,

    @SerializedName("framinghamScore")
    val framinghamScore: Float,

    @SerializedName("framinghamLevel")
    val framinghamLevel: String,

    @SerializedName("framinghamPercentage")
    val framinghamPercentage: String,

    @SerializedName("ascvdScore")
    val ascvdScore: Float,

    @SerializedName("ascvdLevel")
    val ascvdLevel: String,

    @SerializedName("ascvdMessage")
    val ascvdMessage: String,

    @SerializedName("framinghamMessage")
    val framinghamMessage: String,

    @SerializedName("assessmentDate")
    val assessmentDate: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)