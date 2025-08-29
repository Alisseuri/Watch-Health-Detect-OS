package com.chrisp.healthdetect.ui.profile

import kotlin.math.roundToInt
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.dashboard.LottieAnimationPlayer
import com.chrisp.healthdetect.ui.theme.*
import com.chrisp.healthdetect.ui.util.AppBottomNavigation
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInputScreen(
    navController: NavController,
    uiState: UserProfileData,
    viewModel: ProfileViewModel
) {
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = { AppBottomNavigation(navController = navController, currentRoute = "profile") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                InputHeader(
                    name = uiState.name,
                    onNameChange = {
                        viewModel.onNameChange(it)
                        viewModel.clearError() // Clear error when user starts typing
                    }
                )
            }

            item { AverageHeartRateDisplay(avgRate = 87) }

            item {
                PersonalInfoInput(
                    dob = uiState.dob,
                    onDobChange = {
                        viewModel.onDobChange(it)
                        viewModel.clearError()
                    },
                    gender = uiState.gender,
                    onGenderChange = {
                        viewModel.onGenderChange(it)
                        viewModel.clearError()
                    },
                    race = uiState.race,
                    onRaceChange = {
                        viewModel.onRaceChange(it)
                        viewModel.clearError()
                    }
                )
            }

            item {
                HistoryInput(
                    isSmoker = uiState.isSmoker,
                    onSmokerChange = viewModel::onSmokerChange,
                    hasDiabetes = uiState.hasDiabetes,
                    onDiabetesChange = viewModel::onDiabetesChange
                )
            }

            item {
                ParameterInput(
                    totalCholesterol = uiState.totalCholesterol,
                    onTotalCholesterolChange = {
                        viewModel.onTotalCholesterolChange(it)
                        viewModel.clearError()
                    },
                    hdlCholesterol = uiState.hdlCholesterol,
                    onHdlCholesterolChange = {
                        viewModel.onHdlCholesterolChange(it)
                        viewModel.clearError()
                    },
                    systolicBp = uiState.systolicBp,
                    onSystolicBpChange = {
                        viewModel.onSystolicBpChange(it)
                        viewModel.clearError()
                    },
                    oxygenSaturation = uiState.oxygenSaturation,
                    onOxygenSaturationChange = viewModel::onOxygenSaturationChange
                )
            }

            item {
                NutritionInput(
                    height = uiState.height,
                    onHeightChange = {
                        viewModel.onHeightChange(it)
                        viewModel.clearError()
                    },
                    weight = uiState.weight,
                    onWeightChange = {
                        viewModel.onWeightChange(it)
                        viewModel.clearError()
                    }
                )
            }

            item {
                ActivityFactorSection(
                    selectedLevel = uiState.activityLevel,
                    onLevelSelected = viewModel::onActivityLevelChange
                )
            }

            item {
                StressLevelInput(
                    selectedLevel = uiState.stressLevel,
                    onLevelSelected = viewModel::onStressLevelChange
                )
            }

            // Error message display
            error?.let {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, Color.Red)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painterResource(id = R.drawable.arrow_dropdown_mini), // You need to add this icon
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error!!,
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            item {
                ActionButtons(
                    onCancelClick = {
                        if (uiState.name.isNotBlank()) viewModel.setEditMode(false)
                    },
                    onSaveClick = {
                        if (!loading) {
                            viewModel.submitAll()
                        }
                    },
                    loading = loading
                )
            }
        }
    }
}


@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputHeader(
    name: String,
    onNameChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.profile_background),
            contentDescription = "Header Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("Isi nama...") },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(0.7f),
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.9f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.9f)
            )
        )
    }
}

@Composable
fun AverageHeartRateDisplay(avgRate: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LottieAnimationPlayer(
            animationRes = R.raw.heart2,
            modifier = Modifier
                .size(80.dp)
        )
        Spacer(Modifier.width(16.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .weight(1f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "Detak Jantung Rata-Rata",
                    color = Color.Gray,
                    fontSize = 14.sp)
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        "$avgRate",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "BPM",
                        modifier = Modifier
                            .padding(start = 4.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    loading: Boolean = false
) {
    val cancelInteractionSource = remember { MutableInteractionSource() }
    val isCancelPressed by cancelInteractionSource.collectIsPressedAsState()
    val saveInteractionSource = remember { MutableInteractionSource() }
    val isSavePressed by saveInteractionSource.collectIsPressedAsState()

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onCancelClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCancelPressed) HeartRateGreen else Color.Transparent,
                contentColor = if (isCancelPressed) Color.White else HeartRateGreen
            ),
            border = BorderStroke(1.dp, HeartRateGreen),
            interactionSource = cancelInteractionSource,
            enabled = !loading
        ) {
            Text("KEMBALI")
        }
        Spacer(Modifier.width(16.dp))
        Button(
            onClick = onSaveClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSavePressed) HeartRateGreen.copy(alpha = 0.8f) else Color.White,
                contentColor = if (isSavePressed) Color.White else HeartRateGreen
            ),
            interactionSource = saveInteractionSource,
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = HeartRateGreen,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("MENYIMPAN...")
            } else {
                Text("SIMPAN")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonalInfoInput(
    dob: LocalDate?,
    onDobChange: (LocalDate) -> Unit,
    gender: Gender?, onGenderChange: (Gender) -> Unit,
    race: Race?,
    onRaceChange: (Race) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val age = dob?.let { Period.between(it, LocalDate.now()).years }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoBox(
                label = "Usia: ${age?.toString() ?: "--"} tahun",
                modifier = Modifier.weight(1f)
            )
            InfoBox(
                label = dob?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "DD/MM/YYYY",
                icon = R.drawable.calendar,
                onClick = { showDatePicker = true },
                modifier = Modifier.weight(1f),
            )
        }

        CustomDropdown(
            label = "Jenis Kelamin",
            options = Gender.values().map { it.name.replaceFirstChar(Char::titlecase) },
            selectedOption = gender?.name?.replaceFirstChar(Char::titlecase),
            placeholder = "Pilih Jenis",
            onOptionSelected = { onGenderChange(Gender.valueOf(it.uppercase())) }
        )

        CustomDropdown(
            label = "Ras",
            options = Race.values().map { it.displayName },
            selectedOption = race?.displayName,
            placeholder = "Pilih Jenis",
            onOptionSelected = { selectedString -> onRaceChange(Race.values().first { it.displayName == selectedString }) }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis -> onDobChange(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Batal") } }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
private fun HistoryInput(
    isSmoker: YesNo, onSmokerChange: (YesNo) -> Unit,
    hasDiabetes: YesNo,
    onDiabetesChange: (YesNo) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("Riwayat")
        CustomDropdown(
            label = "Merokok",
            options = YesNo.values().map { it.name },
            selectedOption = isSmoker.name,
            placeholder = "Pilih",
            onOptionSelected = { onSmokerChange(YesNo.valueOf(it)) }
        )
        CustomDropdown(
            label = "Diabetes",
            options = YesNo.values().map { it.name },
            selectedOption = hasDiabetes.name,
            placeholder = "Pilih",
            onOptionSelected = { onDiabetesChange(YesNo.valueOf(it)) }
        )
    }
}

@Composable
private fun ParameterInput(totalCholesterol: String, onTotalCholesterolChange: (String) -> Unit, hdlCholesterol: String, onHdlCholesterolChange: (String) -> Unit, systolicBp: String, onSystolicBpChange: (String) -> Unit, oxygenSaturation: String, onOxygenSaturationChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("Parameter")

        ParameterInputField(
            label = "Kolesterol Total",
            value = totalCholesterol,
            onValueChange = onTotalCholesterolChange,
            unit = "mg/dL"
        )

        ParameterInputField(
            label = "HDL Kolesterol",
            value = hdlCholesterol,
            onValueChange = onHdlCholesterolChange,
            unit = "mg/dL"
        )

        ParameterInputField(
            label = "Tekanan Darah Sistolik",
            value = systolicBp,
            onValueChange = onSystolicBpChange,
            unit = "mmHg"
        )

        ParameterInputField(
            label = "Saturasi Oksigen",
            value = oxygenSaturation,
            onValueChange = onOxygenSaturationChange,
            unit = "%"
        )
    }
}

@Composable
private fun NutritionInput(
    height: String,
    onHeightChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit
) {
    Column {
        SectionTitle("Nutrisi")
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NutritionInputField(
                label = "Tinggi Badan",
                value = height,
                onValueChange = onHeightChange,
                unit = "cm",
                modifier = Modifier.weight(1f)
            )

            NutritionInputField(
                label = "Berat Badan",
                value = weight,
                onValueChange = onWeightChange,
                unit = "kg",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StressLevelInput(
    selectedLevel: StressLevel,
    onLevelSelected: (StressLevel) -> Unit
) {
    val sedangColor = Color(0xFFFFA73B)

    val levelColor = when (selectedLevel) {
        StressLevel.RINGAN -> Color(0xFF0EBE4A)
        StressLevel.SEDANG -> Color(0xFFE56035)
        StressLevel.BERAT -> Color(0xFFE94D64)
    }
    Column(
        horizontalAlignment = Alignment.End
    ) {
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
fun NutritionInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = modifier
    ) {
        Text(
            label,
            color = DarkText,
            modifier = Modifier
                .padding(start = 4.dp, bottom = 8.dp),
            fontWeight = FontWeight.SemiBold
        )
        BasicTextField(
            value = value,
            onValueChange = { newValue -> if (newValue.all { it.isDigit() }) onValueChange(newValue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .focusRequester(focusRequester),
            textStyle = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                        .height(80.dp)
                        .clickable { focusRequester.requestFocus() }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        innerTextField()
                    }
                    Text(
                        text = unit,
                        color = Color.Gray,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                    )
                }
            }
        )
    }
}

@Composable
fun ActivityIcon(level: ActivityLevel, isSelected: Boolean, onClick: () -> Unit) {
    val iconRes = when (level) {
        ActivityLevel.BEDREST -> if (isSelected) R.drawable.bedrest_clicked2 else R.drawable.bedrest
        ActivityLevel.RINGAN -> if (isSelected) R.drawable.ringan_clicked2 else R.drawable.ringan
        ActivityLevel.SEDANG -> if (isSelected) R.drawable.sedang_clicked2 else R.drawable.sedang
        ActivityLevel.BERAT -> if (isSelected) R.drawable.berat_clicked2 else R.drawable.berat
    }
    val number = level.ordinal + 1
    val color by animateColorAsState(if (isSelected) DarkText else Color.Gray, label = "iconColor")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(if (isSelected) Color.White else Color.Transparent, CircleShape),
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
fun ActivityFactorSection(
    selectedLevel: ActivityLevel,
    onLevelSelected: (ActivityLevel) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionTitle("Faktor Aktivitas")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActivityIcon(
                level = ActivityLevel.BEDREST,
                isSelected = selectedLevel == ActivityLevel.BEDREST,
                onClick = { onLevelSelected(ActivityLevel.BEDREST) }
            )

            ActivityIcon(
                level = ActivityLevel.RINGAN,
                isSelected = selectedLevel == ActivityLevel.RINGAN,
                onClick = { onLevelSelected(ActivityLevel.RINGAN) }
            )

            ActivityIcon(
                level = ActivityLevel.SEDANG,
                isSelected = selectedLevel == ActivityLevel.SEDANG,
                onClick = { onLevelSelected(ActivityLevel.SEDANG) }
            )

            ActivityIcon(
                level = ActivityLevel.BERAT,
                isSelected = selectedLevel == ActivityLevel.BERAT,
                onClick = { onLevelSelected(ActivityLevel.BERAT) }
            )
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
    placeholder: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "arrowRotation")

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoBox(
            label = label,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedOption ?: placeholder,
                    color = if (selectedOption == null) Color.Gray else DarkText
                )

                Icon(
                    painter = painterResource(id = R.drawable.arrow_dropdown),
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.rotate(rotationAngle),
                    tint = OxygenBlue
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = {
                        onOptionSelected(option)
                        expanded = false
                    })
                }
            }
        }
    }
}
