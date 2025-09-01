package com.chrisp.healthdetect.ui.steps

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
//import androidx.wear.compose.material.Divider
import androidx.wear.compose.material.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StepsResultScreen(
    result: StepsResult,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Activity",
            color = Color(0xFF11E1F5),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Total Steps",
            fontSize = 18.sp,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${result.totalSteps}",
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

//        Divider(modifier = Modifier.width(100.dp), color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Text(formatDuration(result.durationSeconds), color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
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
fun StepsResultScreenPreview() {
    StepsResultScreen(
        result = StepsResult(
            totalSteps = 1100,
            durationSeconds = 12620,
            finishedAt = LocalDateTime.now()
        ),
        onFinish = {}
    )
}