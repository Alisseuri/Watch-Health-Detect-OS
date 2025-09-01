package com.chrisp.healthdetect.ui.heartrate

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HeartRateResultScreen(result: HeartRateResult, onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Heart Rate",
            color = Color(0xFFFE3154),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Average", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFE3154), Color(0xFFFBEB73))
                        ),
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("${result.averageBpm}")
                }
                withStyle(
                    style = SpanStyle(fontSize = 24.sp)
                ) {
                    append(" BPM")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(formatDuration(result.durationSeconds), color = Color.Gray)
//        Divider(modifier = Modifier.width(100.dp).padding(vertical = 4.dp), color = Color.Gray)
        Text(formatFinishTime(result.finishedAt), color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onFinish) {
            Text("Selesai")
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val hr = seconds / 3600; val min = (seconds % 3600) / 60; val sec = seconds % 60
    return "${hr} hr ${min} mins ${sec} secs"
}
private fun formatFinishTime(dateTime: LocalDateTime): String {
    return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy | HH.mm"))
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun HeartRateResultScreenPreview() {
    HeartRateResultScreen(
        result = HeartRateResult(
            averageBpm = 90,
            durationSeconds = 7350,
            finishedAt = LocalDateTime.now()
        ),
        onFinish = {}
    )
}