package com.chrisp.healthdetect.ui.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun ageToDobString(age: Int): String {
    val dob = LocalDate.now().minusYears(age.toLong())
    return dob.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}