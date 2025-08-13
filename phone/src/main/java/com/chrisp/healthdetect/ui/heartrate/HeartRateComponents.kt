package com.chrisp.healthdetect.ui.heartrate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.chrisp.healthdetect.ui.util.formatTimeAgo

@Composable
fun MainBpmDisplay(bpm: Int, timestamp: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        LottieAnimationPlayer(
            animationRes = R.raw.heart2,
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$bpm",
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    lineHeight = 70.sp
                )
                Text(
                    "BPM",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFD172E),
                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                )
            }
            Text(
                text = formatTimeAgo(timestamp),
                color = LightGrayText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 8.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.heart_line),
                contentDescription = "Garis BPM",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 10.dp, end = 40.dp)
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                "$value",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                lineHeight = 38.sp
            )

            Text(
                "BPM",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
        }
    }
}

@Composable
fun StatsRow(avg:Int, min: Int, max: Int) {
    Column(
        modifier = Modifier
            .padding(vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(end = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(label = "Average", value = avg, color = Color(0xFFFD172E))
            StatItem(label = "Min", value = min, color = Color(0xFFFD172E))
            StatItem(label = "Max", value = max, color = Color(0xFFFD172E))
        }
        Divider(
            modifier = Modifier.padding(top = 24.dp),
            color = Color.LightGray.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = DarkText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    )
}

@Composable
fun InterpretationCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, OxygenBlue),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DetailInterpretationSection(isExpanded: Boolean, onToggle: () -> Unit) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "arrowRotation"
    )

    Column(modifier = Modifier.padding(top = 24.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, HeartRateGreen),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Detail Interpretasi Data Detak Jantung",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 1.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.arrow_dropdown),
                    contentDescription = "Toggle Detail",
                    modifier = Modifier.rotate(rotationAngle)
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            InterpretationTable()
        }
    }
}

@Composable
fun InterpretationTable() {
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(HeartRateGreen)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeartRateGreen)
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            TableCell(text = "HR (BPM)", weight = 0.25f, isHeader = true)
            TableCell(text = "Interpretasi", weight = 0.3f, isHeader = true)
            TableCell(text = "Kemungkinan Penyebab", weight = 0.45f, isHeader = true)
        }
        Divider(color = Color.White.copy(alpha = 0.5f))

        heartRateInterpretationTable.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell(text = item.range, weight = 0.25f)
                TableCell(text = item.interpretation, weight = 0.3f)
                TableCell(text = item.possibleCauses, weight = 0.45f)
            }
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float, isHeader: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight)
            .padding(horizontal = 4.dp),
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        color = if (isHeader) Color.White else DarkText,
        fontSize = 14.sp
    )
}

@Preview(showBackground = true)
@Composable
fun HeartRateDetailScreenPrev() {
        HeartRateDetailScreen(
            currentBpm = 55,
            avgBpm = 97,
            minBpm = 42,
            maxBpm = 120,
            lastUpdateTimestamp = System.currentTimeMillis(),
            onBackClick = {}
        )
}