package com.chrisp.healthdetect.ui.oxygen

data class OxygenInterpretation(
    val range: String,
    val interpretation: String,
    val generalCondition: String,
    val description: String
)

val oxygenInterpretationTable = listOf(
    OxygenInterpretation(
        range = "95% - 100%",
        interpretation = "Normal",
        generalCondition = "Tidak ada tanda hipoksia dan tidak memerlukan intervensi tambahan oksigen.",
        description = "Rentang SpO₂ berupa 98% menampilkan hasil normal."
    ),

    OxygenInterpretation(
        range = "90% - 94%",
        interpretation = "Rendah Ringan (Hipoksia Ringan)",
        generalCondition = "Mulai menunjukkan gejala ringan seperti napas pendek, lemas, Perlu pemantauan lebih lanjut. Bisa jadi normal pada pasien dengan penyakit paru kronik seperti COPD.",
        description = "Rentang SpO₂ berupa 90%-94% berada di bawah kisaran normal, menunjukkan hipoksia ringan."
    ),

    OxygenInterpretation(
        range = "85% - 89%",
        interpretation = "Hipoksia Sedang",
        generalCondition = "Menandakan defisiensi oksigen yang cukup signifikan. Biasanya membutuhkan oksigen tambahan dan evaluasi klinis segera.",
        description = "SpO₂ Anda menunjukkan kekurangan oksigen sedang yang mungkin memerlukan perhatian medis."
    ),

    OxygenInterpretation(
        range = "< 85%",
        interpretation = "Hipoksia Berat / Kondisi Kritis",
        generalCondition = "Risiko tinggi kerusakan organ, termasuk otak. Kondisi gawat darurat; perlu intervensi medis segera.",
        description = "SpO₂ Anda berada pada level kritis. Ini adalah kondisi darurat yang memerlukan intervensi medis segera."
    ),
)

fun getOxygenInterpretation(spo2: Int): OxygenInterpretation {
    return when {
        spo2 >= 95 -> oxygenInterpretationTable[0]
        spo2 in 90..94 -> oxygenInterpretationTable[1]
        spo2 in 85..89 -> oxygenInterpretationTable[2]
        else -> oxygenInterpretationTable[3]
    }
}