package com.chrisp.healthdetect.ui.profile

enum class Gender { PRIA, WANITA }
enum class YesNo { YA, TIDAK }
enum class ActivityLevel(val displayName: String) {
    BEDREST("BEDREST"),
    RINGAN("RINGAN"),
    SEDANG("SEDANG"),
    BERAT("BERAT")
}
enum class StressLevel(val displayName: String) {
    RINGAN("RINGAN"),
    SEDANG("SEDANG"),
    BERAT("BERAT")
}