package com.chrisp.healthdetect.ui.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
//import androidx.wear.compose.material.Divider
import androidx.wear.compose.material.Text
import java.time.LocalDateTime

@Composable
fun StepsResultScreen(
    result: StepsResult,
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> ResultPage("Activity", "Calories Burned", "${result.caloriesBurned}", "", Color(0xFF11E1F5), result)
                1 -> ResultPage("Activity", "Total Steps", "${result.totalSteps}", "", Color(0xFF11E1F5), result)
                2 -> ResultPage("Heart Rate", "Average", "${result.averageBpm}", "BPM", Color(0xFFFE3154), result)
            }
        }
        PagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(horizontal = 24.dp, vertical = 8.dp)
        )
        Button(
            onClick = onFinish,
            modifier = Modifier
                .height(25.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 1.dp)
        ) {
            Text(
                "Selesai",
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ResultPage(
    title: String,
    subtitle: String,
    value: String, unit: String,
    titleColor: Color, result: StepsResult,
    viewModel: StepsViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            title,
            color = titleColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))

        Text(
            subtitle,
            fontSize = 12.sp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White)) { append(value) }
                if (unit.isNotBlank()) {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 16.sp,
                            color = Color.White)
                    ) { append(" $unit") }
                }
            }
        )
        Text(
            viewModel.formatDuration(result.durationSeconds),
            color = Color.Gray,
            fontSize = 10.sp
        )
        Text(
            viewModel.formatFinishTime(result.finishedAt),
            color = Color.Gray,
            fontSize = 10.sp
        )
    }
}

@Composable
fun PagerIndicator(
    pagerState: androidx.compose.foundation.pager.PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.Gray
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration) activeColor else inactiveColor
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)
            )
        }
    }
}

@Composable
fun RequestPermissionScreen(onPermissionRequest: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Izin sensor tubuh diperlukan...", textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onPermissionRequest) { Text("Berikan Izin") }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun ActivityResultScreenPreview() {
        val dummyResult = StepsResult(
            caloriesBurned = 150,
            totalSteps = 1100,
            averageBpm = 120,
            durationSeconds = 12620,
            finishedAt = LocalDateTime.now()
        )
        StepsResultScreen(
            result = dummyResult,
            onFinish = {}
        )
}

//private fun formatDuration(seconds: Long): String {
//    val hr = seconds / 3600; val min = (seconds % 3600) / 60; val sec = seconds % 60
//    return "${hr} hr ${min} mins ${sec} secs"
//}
//private fun formatFinishTime(dateTime: LocalDateTime): String {
//    return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy | HH.mm"))
//}