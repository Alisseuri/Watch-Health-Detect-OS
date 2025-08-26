package com.chrisp.healthdetect.ui.bmi

import androidx.compose.ui.graphics.Color
import com.chrisp.healthdetect.ui.theme.*

data class BmiInterpretation(
    val value: Float,
    val category: String,
    val color: Color,
    val note: String
)

fun getBmiInterpretation(weightKg: Float, heightCm: Float): BmiInterpretation {
    if (heightCm <= 0f || weightKg <= 0f) {
        return BmiInterpretation(
            0f,
            "INVALID",
            Color.Gray,
            "Data tinggi dan berat badan tidak valid."
        )
    }
    val heightM = heightCm / 100
    val bmi = weightKg / (heightM * heightM)

    return when {
        bmi < 17.0 -> BmiInterpretation(
            value = bmi,
            category = "SANGAT KURUS",
            color = BmiSangatKurus,
            note = "Anda berada dalam kategori sangat kurus. Disarankan untuk berkonsultasi dengan dokter atau ahli gizi untuk meningkatkan asupan nutrisi yang sehat."
        )

        bmi < 18.5 -> BmiInterpretation(
            value = bmi,
            category = "KURUS",
            color = BmiKurus2,
            note = "Anda berada dalam kategori kurus. Mempertahankan pola makan seimbang dan teratur dapat membantu mencapai berat badan ideal."
        )

        bmi < 25.0 -> BmiInterpretation(
            value = bmi,
            category = "NORMAL",
            color = BmiNormal2,
            note = "Selamat! Berat badan Anda berada dalam rentang normal. Pertahankan gaya hidup sehat Anda dengan pola makan seimbang dan olahraga teratur."
        )

        bmi < 17.0 -> BmiInterpretation(
            value = bmi,
            category = "GEMUK (OVERWEIGHT)",
            color = BmiGemuk,
            note = "Anda berada dalam kategori gemuk (overweight). Disarankan untuk meningkatkan aktivitas fisik dan memperhatikan asupan kalori untuk mencegah risiko kesehatan."
        )

        else -> BmiInterpretation(
            value = bmi,
            category = "OBESITAS",
            color = BmiObesitas,
            note = "Anda berada dalam kategori obesitas. Sangat disarankan untuk berkonsultasi dengan profesional kesehatan untuk menyusun rencana pengelolaan berat badan yang aman."
        )
    }
}