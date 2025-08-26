package com.chrisp.healthdetect.ui.bmi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.profile.Gender
import com.chrisp.healthdetect.ui.theme.*

@Composable
fun BmiResultCard(interpretation: BmiInterpretation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, OxygenBlue),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format("%.1f", interpretation.value),
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                color = interpretation.color,
                lineHeight = 70.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = interpretation.category,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = interpretation.color
            )
        }
    }
}

@Composable
fun UserDataSummary(
    age: Int,
    gender: String,
    height: String,
    weight: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(0.8f),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryRow(
            label = "Umur",
            value = "$age"
        )
        Divider(color = Color.LightGray.copy(alpha = 0.5f))

        SummaryRow(
            label = "Jenis Kelamin",
            value = gender
        )
        Divider(color = Color.LightGray.copy(alpha = 0.5f))

        SummaryRow(
            label = "Tinggi Badan",
            value = height
        )
        Divider(color = Color.LightGray.copy(alpha = 0.5f))

        SummaryRow(
            label = "Berat Badan",
            value = weight
        )
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = OxygenBlue,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = value,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }

}

@Composable
fun NotesCard(interpretation: BmiInterpretation) {
    val dynamicColor = interpretation.color

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, dynamicColor),
        colors = CardDefaults.cardColors(
            containerColor = dynamicColor.copy(alpha = 0.05f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Catatan",
                color = dynamicColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = interpretation.note,
                color = dynamicColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BmiDetailScreennPreview() {
    BmiDetailScreen(
        age = 25,
        gender = Gender.WANITA,
        heightCm = 160,
        weightKg = 50,
        onBackClick = {}
    )
}

@Preview(name = "Notes Card - Normal", showBackground = true)
@Composable
fun NotesCardNormalPreview() {
    NotesCard(
        interpretation = getBmiInterpretation(weightKg = 60f, heightCm = 165f)
    )
}

@Preview(name = "Notes Card - Obesitas", showBackground = true)
@Composable
fun NotesCardObesePreview() {
    NotesCard(
        interpretation = getBmiInterpretation(weightKg = 90f, heightCm = 165f)
    )
}