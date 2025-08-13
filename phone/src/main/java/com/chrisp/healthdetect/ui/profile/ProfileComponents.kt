package com.chrisp.healthdetect.ui.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.heartrate.SectionTitle
import com.chrisp.healthdetect.ui.theme.DarkText
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ProfileHeader(username: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_background),
            contentDescription = "Background Profil",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(username, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "#0000000",
                    modifier = Modifier
                        .background(DarkText.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White
                )
            }
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoSection(
    dob: LocalDate?,
    onDobSelected: (LocalDate) -> Unit,
    selectedGender: Gender?,
    onGenderSelect: (Gender) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val age = dob?.let { Period.between(it, LocalDate.now()).years }

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        InfoBox(
            label = "Usia: ${age?.let { "$it tahun" } ?: "Pilih tanggal"}",
            modifier = Modifier.weight(1f)
        )
        InfoBox(
            label = dob?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "DD/MM/YYYY",
            icon = R.drawable.calendar,
            onClick = { showDatePicker = true },
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(Modifier.height(16.dp))
    CustomDropdown(
        label = "Jenis Kelamin",
        options = Gender.values().map { it.name.replaceFirstChar(Char::titlecase) },
        selectedOption = selectedGender?.name?.replaceFirstChar(Char::titlecase),
        onOptionSelected = {
            onGenderSelect(Gender.valueOf(it.uppercase()))
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton (
                    onClick = {
                        val selectedDateMillis = datePickerState.selectedDateMillis
                        if (selectedDateMillis != null) {
                            val selectedLocalDate = Instant.ofEpochMilli(selectedDateMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDobSelected(selectedLocalDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun HistorySection(
    isSmoker: YesNo,
    onSmokerChange: (YesNo) -> Unit,
    hasDiabetes: YesNo,
    onDiabetesChange: (YesNo) -> Unit
) {
    CustomDropdown(
        label = "Merokok",
        options = YesNo.values().map { it.name },
        selectedOption = isSmoker.name,
        onOptionSelected = { onSmokerChange(YesNo.valueOf(it)) }
    )
    Spacer(Modifier.height(16.dp))
    CustomDropdown(
        label = "Diabetes",
        options = YesNo.values().map { it.name },
        selectedOption = hasDiabetes.name,
        onOptionSelected = { onDiabetesChange(YesNo.valueOf(it)) }
    )
}

@Composable
fun ParameterSection(
    totalCholesterol: String, onTotalCholesterolChange: (String) -> Unit,
    hdlCholesterol: String, onHdlCholesterolChange: (String) -> Unit,
    systolicBp: String, onSystolicBpChange: (String) -> Unit,
    oxygenSaturation: String, onOxygenSaturationChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("Parameter")
        ParameterInputField(label = "Kolesterol Total", value = totalCholesterol, onValueChange = onTotalCholesterolChange, unit = "mg/dL")
        ParameterInputField(label = "HDL Kolesterol", value = hdlCholesterol, onValueChange = onHdlCholesterolChange, unit = "mg/dL")
        ParameterInputField(label = "Tekanan Darah Sistolik", value = systolicBp, onValueChange = onSystolicBpChange, unit = "mmHg")
        ParameterInputField(label = "Saturasi Oksigen", value = oxygenSaturation, onValueChange = onOxygenSaturationChange, unit = "SpO₂")
    }
}

@Composable
fun NutritionSection(
    height: String,
    onHeightChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit
) {
    Column {
        SectionTitle("Nutrisi")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            NutritionInputField(label = "Tinggi Badan", value = height, onValueChange = onHeightChange, unit = "cm", modifier = Modifier.weight(1f))
            NutritionInputField(label = "Berat Badan", value = weight, onValueChange = onWeightChange, unit = "kg", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StressLevelSection(
    selectedLevel: StressLevel,
    onLevelSelected: (StressLevel) -> Unit
) {
    val sedangColor = Color(0xFFFFA73B)

    val levelColor = when (selectedLevel) {
        StressLevel.RINGAN -> Color(0xFF0EBE4A)
        StressLevel.SEDANG -> Color(0xFFE56035)
        StressLevel.BERAT -> Color(0xFFE94D64)
    }
    Column(horizontalAlignment = Alignment.End) {
        SectionTitle("Stress Level")
        StressSlider(
            selectedLevel = selectedLevel,
            onLevelSelected = onLevelSelected)
        Text(
            text = selectedLevel.displayName,
            fontWeight = FontWeight.Bold,
            color = levelColor,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ParameterInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoBox(label = label, modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(0.7f),
            placeholder = { Text("---") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = HeartRateGreen

            )
        )
        Text(unit, modifier = Modifier.weight(0.4f))
    }
}

@Composable
fun NutritionInputField(label: String, value: String, onValueChange: (String) -> Unit, unit: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, color = DarkText, modifier = Modifier.padding(bottom = 8.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()
                    }
                    Text(unit, color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun ActivityIcon(level: ActivityLevel, isSelected: Boolean, onClick: () -> Unit) {
    val iconRes = when (level) {
        ActivityLevel.BEDREST -> if (isSelected) R.drawable.bedrest_clicked else R.drawable.bedrest
        ActivityLevel.RINGAN -> if (isSelected) R.drawable.ringan_clicked else R.drawable.ringan
        ActivityLevel.SEDANG -> if (isSelected) R.drawable.sedang_clicked else R.drawable.sedang
        ActivityLevel.BERAT -> if (isSelected) R.drawable.berat_clicked else R.drawable.berat
    }
    val number = level.ordinal + 1
    val color by animateColorAsState(if (isSelected) DarkText else Color.LightGray, label = "iconColor")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(if (isSelected) HeartRateGreen.copy(alpha = 0.1f) else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(id = iconRes),
                contentDescription = level.name,
                modifier = Modifier.size(36.dp),
                tint = Color.Unspecified
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(number.toString(), color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StressSlider(
    selectedLevel: StressLevel,
    onLevelSelected: (StressLevel) -> Unit
) {
    val brush = Brush.horizontalGradient(listOf(Color(0xFF76C893), Color(0xFFFFCA28), Color(0xFFE94D64)))
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val sectionWidth = size.width / 3f
                    when {
                        offset.x < sectionWidth -> onLevelSelected(StressLevel.RINGAN)
                        offset.x < sectionWidth * 2 -> onLevelSelected(StressLevel.SEDANG)
                        else -> onLevelSelected(StressLevel.BERAT)
                    }
                }
            }
    ) {
        drawRoundRect(brush = brush, cornerRadius = CornerRadius(12.dp.toPx()))

        val indicatorX = when (selectedLevel) {
            StressLevel.RINGAN -> size.width / 6f
            StressLevel.SEDANG -> size.width / 2f
            StressLevel.BERAT -> size.width * 5 / 6f
        }
        drawCircle(
            color = Color.White,
            radius = (size.height / 2f) + 4.dp.toPx(),
            center = Offset(indicatorX, size.height / 2f)
        )
        drawCircle(
            color = when (selectedLevel) {
                StressLevel.RINGAN -> Color(0xFF76C893)
                StressLevel.SEDANG -> Color(0xFFFFA73B)
                StressLevel.BERAT -> Color(0xFFE94D64)
            },
            radius = (size.height / 2f),
            center = Offset(indicatorX, size.height / 2f)
        )
    }
}

@Composable
fun ActivityFactorSection(selectedLevel: ActivityLevel, onLevelSelected: (ActivityLevel) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SectionTitle("Faktor Aktivitas")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActivityIcon(level = ActivityLevel.BEDREST, isSelected = selectedLevel == ActivityLevel.BEDREST, onClick = { onLevelSelected(ActivityLevel.BEDREST) })
            ActivityIcon(level = ActivityLevel.RINGAN, isSelected = selectedLevel == ActivityLevel.RINGAN, onClick = { onLevelSelected(ActivityLevel.RINGAN) })
            ActivityIcon(level = ActivityLevel.SEDANG, isSelected = selectedLevel == ActivityLevel.SEDANG, onClick = { onLevelSelected(ActivityLevel.SEDANG) })
            ActivityIcon(level = ActivityLevel.BERAT, isSelected = selectedLevel == ActivityLevel.BERAT, onClick = { onLevelSelected(ActivityLevel.BERAT) })
        }
        Text(
            text = selectedLevel.displayName,
            fontWeight = FontWeight.Bold,
            color = HeartRateGreen,
            modifier = Modifier
                .padding(top = 8.dp)
                .background(HeartRateGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun InfoBox(
   label: String,
   modifier: Modifier = Modifier,
   icon: Int? = null,
   onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = if (label.contains("Pilih") || label.contains("DD/MM")) Color.Gray else DarkText)
        icon?.let {
            Icon(painterResource(id = it), contentDescription = null, tint = HeartRateGreen)
        }
    }
}

@Composable
fun CustomDropdown(
   label: String,
   options: List<String>,
   selectedOption: String?,
   onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        InfoBox(label = label, modifier = Modifier.weight(1f))

        Box(modifier = Modifier.weight(1f)) {
            InfoBox(
                label = selectedOption ?: "Pilih Jenis",
                icon = R.drawable.arrow_dropdown_mini,
                onClick = { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}