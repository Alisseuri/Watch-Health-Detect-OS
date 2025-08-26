package com.chrisp.healthdetect.ui.activity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chrisp.healthdetect.ui.nutrition.NutritionStatusScreen
import com.chrisp.healthdetect.ui.theme.BackgroundGray
import com.chrisp.healthdetect.ui.util.AppBottomNavigation

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityScreen(
    navController: NavController,
    viewModel: ActivityViewModel = viewModel()
) {
    val uiState by rememberUpdatedState(viewModel.uiState)
    val selectedDaySummary = uiState.selectedDaySummary

    val selectedDayIndex = uiState.weeklyData.indexOfFirst { it.date == uiState.selectedDate }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = { AppBottomNavigation(navController = navController, currentRoute = "aktivitas") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                PageHeader(
                    selectedDate = uiState.selectedDate,
                    onDateSelected = { viewModel.onMonthYearChanged(it) }
                )
            }

            item {
                ActivityChartCard(
                    weeklyData = uiState.weeklyData,
                    onDaySelected = { viewModel.onDaySelected(it) },
                    selectedDayIndex = selectedDayIndex,
                    maxValue = uiState.chartMaxValue,
                    yAxisLabels = uiState.yAxisLabels
                )
            }

            item {
                HeartRateSummary(
                    avgHeartRate = selectedDaySummary?.avgHeartRate ?: 0,
                    restingHeartRate = selectedDaySummary?.restingHeartRate ?: 0
                )
            }

            item {
                Divider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color.White.copy(alpha = 0.5f),
                    thickness = 3.dp
                )
            }

            item {
                StepsCard(
                    steps = selectedDaySummary?.steps?.toInt() ?: 0,
                    date = selectedDaySummary?.date
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun PageHeader(
    selectedDate: java.time.LocalDate,
    onDateSelected: (java.time.LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Aktivitas",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("*") }
                    append("grafik diakumulasi per minggu")
                },
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        MonthYearPicker(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )
    }
}
