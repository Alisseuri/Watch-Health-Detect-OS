package com.chrisp.healthdetect.ui.nutrition

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


fun calculateBmi(weightKg: Float, heightCm: Float): BmiResult {
    if (heightCm == 0f) return BmiResult(0f, "INVALID", Color.Gray, R.drawable.bmi_normal)
    val heightM = heightCm / 100
    val bmi = weightKg / (heightM * heightM)

    return when {
        bmi < 17.0 -> BmiResult(bmi, "SANGAT KURUS", BmiSangatKurus, R.drawable.bmi_severe_thin)
        bmi < 18.5 -> BmiResult(bmi, "KURUS", BmiKurus, R.drawable.bmi_moderate_thin)
        bmi < 25.0 -> BmiResult(bmi, "NORMAL", BmiNormal, R.drawable.bmi_normal)
        bmi < 27.0 -> BmiResult(bmi, "GEMUK", BmiGemuk, R.drawable.bmi_overweight)
        else -> BmiResult(bmi, "OBESITAS", BmiObesitas, R.drawable.bmi_obesity)
    }
}

fun calculateBmr(weightKg: Float, heightCm: Float, age: Int, isMale: Boolean): Float {
    return 1500f
}

fun calculateTee(bmr: Float): Float {
    return bmr * 1.2f
}

fun calculateMacros(tee: Float, weightKg: Float): MacroNutrients {
    return MacroNutrients(proteinGrams = 60, carbsGrams = 250, fatGrams = 50) // Placeholder
}

fun calculateIdealWeight(heightCm: Float): Float {
    return heightCm - 100 - ((heightCm - 100) * 0.10f)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatIdealWeightTimestamp(): String {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy | HH.mm", Locale("id", "ID"))
    return now.format(formatter)
}