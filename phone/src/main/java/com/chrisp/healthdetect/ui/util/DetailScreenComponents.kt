package com.chrisp.healthdetect.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.theme.DarkText
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import com.chrisp.healthdetect.ui.theme.OxygenBlue
import retrofit2.http.Header

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
fun DetailInterpretationSection(
    title: String,
    borderColor: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    tableContent: @Composable () -> Unit
) {
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "arrowRotation")

    Column(modifier = Modifier.padding(top = 24.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, borderColor),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.arrow_dropdown),
                    contentDescription = "Toggle Detail",
                    modifier = Modifier.rotate(rotationAngle)
                )
            }
        }
        AnimatedVisibility(visible = isExpanded) {
            tableContent()
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float, isHeader: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight).padding(horizontal = 4.dp),
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        color = if (isHeader) Color.White else DarkText,
        fontSize = 14.sp
    )
}