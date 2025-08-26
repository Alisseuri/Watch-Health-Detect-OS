package com.chrisp.healthdetect.ui.oxygen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.dashboard.LottieAnimationPlayer
import com.chrisp.healthdetect.ui.theme.DarkText
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import com.chrisp.healthdetect.ui.theme.LightGrayText
import com.chrisp.healthdetect.ui.theme.OxygenBlue
import com.chrisp.healthdetect.ui.util.TableCell
import com.chrisp.healthdetect.ui.util.formatTimeAgo

@Composable
fun MainSpo2Display(spo2: Int, timestamp: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LottieAnimationPlayer(
            animationRes = R.raw.oxygen2,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$spo2",
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    lineHeight = 70.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    "%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = OxygenBlue,
                    modifier = Modifier.padding(start = 8.dp, bottom = 10.dp)
                )
            }
            Text(
                text = formatTimeAgo(timestamp),
                color = LightGrayText,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 8.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.oxygen_line),
                contentDescription = "Garis SPO2",
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp)
            )
        }
    }
}
@Composable
fun OxygenInterpretationTable() {
    Column(
        modifier = Modifier.padding(top = 8.dp).clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(HeartRateGreen).padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            TableCell(text = "(SpO₂)", weight = 0.25f, isHeader = true)
            TableCell(text = "Interpretasi", weight = 0.3f, isHeader = true)
            TableCell(text = "Kondisi Umum", weight = 0.45f, isHeader = true)
        }
        Divider(color = Color.White.copy(alpha = 0.5f))
        oxygenInterpretationTable.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell(text = item.range, weight = 0.25f)
                TableCell(text = item.interpretation, weight = 0.3f)
                TableCell(text = item.generalCondition, weight = 0.45f)
            }
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun DetailOxygenInterpretationSection(isExpanded: Boolean, onToggle: () -> Unit) {}


@Preview(showBackground = true)
@Composable
fun OxygenDetailScreenPreview() {
    OxygenDetailScreen(
        currentSpo2 = 98,
        lastUpdateTimestamp = System.currentTimeMillis(),
        onBackClick = {}
    )
}