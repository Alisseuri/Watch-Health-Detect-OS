package com.chrisp.healthdetect.ui.activity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.dashboard.LottieAnimationPlayer
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import com.chrisp.healthdetect.ui.theme.OxygenBlue
import com.chrisp.healthdetect.ui.theme.ProteinColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearPicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("MMMM - yyyy", Locale("id", "ID"))

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(OxygenBlue)
            .clickable{ showDatePicker = true }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(formatter.format(selectedDate), color = Color.White)
        Spacer(Modifier.width(8.dp))

        Icon(
            painterResource(id = R.drawable.calendar3),
            contentDescription = "Pilih Bulan",
            tint = Color.Unspecified
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate())
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun ActivityChartCard(
    weeklyData: List<DailySummary>,
    onDaySelected: (Int) -> Unit,
    selectedDayIndex: Int,
    maxValue: Float,
    yAxisLabels: List<String>
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Pilih Satu Hari untuk Melihat Hasil Aktivitas", color = Color.Gray)
            Spacer(Modifier.height(16.dp))
            if (weeklyData.isNotEmpty()) {
                CustomBarChart(
                    data = weeklyData.map { it.steps },
                    onBarClick = onDaySelected,
                    selectedBarIndex = selectedDayIndex,
                    maxValue = maxValue,
                    yAxisLabels = yAxisLabels
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Data tidak tersedia")
                }
            }
        }
    }
}

@Composable
private fun CustomBarChart(
    data: List<Float>,
    onBarClick: (Int) -> Unit,
    selectedBarIndex: Int,
    maxValue: Float,
    yAxisLabels: List<String>
) {
    val maxValue = 4000f
    val days = listOf("SEN", "SEL", "RAB", "KAM", "JUM", "SAB", "MIN")

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFE0F7FA).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                yAxisLabels.forEach { label ->
                    Text(
                        label,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    val lineCount = 3
                    for (i in 1..lineCount) {
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(0f, size.height * (i.toFloat() / (lineCount + 1))),
                            end = Offset(size.width, size.height * (i.toFloat() / (lineCount + 1))),
                            pathEffect = pathEffect
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxSize().pointerInput(data) {
                        detectTapGestures { offset ->
                            val barWidthPx = size.width / 7f
                            val index = (offset.x / barWidthPx).toInt().coerceIn(0, 6)
                            onBarClick(index)
                        }
                    },
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    data.forEachIndexed { index, value ->
                        Bar(
                            value = value,
                            maxValue = maxValue,
                            isSelected = index == selectedBarIndex
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            days.forEach { day ->
                Text(
                    day,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun RowScope.Bar(
    value: Float,
    maxValue: Float,
    isSelected: Boolean
) {
    val barHeightFraction = (value / maxValue).coerceIn(0f, 1f)
    val barColor = if (isSelected) OxygenBlue else HeartRateGreen

    Box(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
            .fillMaxHeight(barHeightFraction)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(barColor)
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-8).dp)
                    .width(25.dp)
                    .height(8.dp)
                    .background(Color.Black, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun HeartRateSummary(
    avgHeartRate: Int,
    restingHeartRate: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LottieAnimationPlayer(
            animationRes = R.raw.heart2,
            modifier = Modifier
                .size(100.dp)
        )
        Spacer(Modifier.width(16.dp))

        HeartRateStat(
            label = "Rata-rata",
            value = avgHeartRate,
            color = Color.Black
        )
        Spacer(Modifier.width(32.dp))

        HeartRateStat(
            label = "Istirahat",
            value = restingHeartRate,
            color = Color.Black
        )
    }
}

@Composable
private fun HeartRateStat(
    label: String,
    value: Int,
    color: Color
) {
    Column {
        Text(
            label,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                "$value",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 38.sp
            )
            Text(
                "BPM",
                modifier = Modifier
                    .padding(start = 4.dp, bottom = 4.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StepsCard(
    steps: Int,
    date: LocalDate?
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("id", "ID"))
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Anda berjalan selama",
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$steps",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 50.sp
                    )
                    Text(
                        "Langkah",
                        modifier = Modifier
                            .padding(start = 6.dp, bottom = 6.dp)
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "Hari",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    date?.format(formatter) ?: "--/--/----",
                    color = Color.Gray
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ActivityPreview() {
    ActivityScreen(navController = rememberNavController())
}
