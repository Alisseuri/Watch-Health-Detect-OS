package com.chrisp.healthdetect.ui.nutrition

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.theme.CarbsColor
import com.chrisp.healthdetect.ui.theme.FatColor
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import com.chrisp.healthdetect.ui.theme.OxygenBlue
import com.chrisp.healthdetect.ui.theme.ProteinColor

@Composable
fun BmiCard(
    result: BmiResult,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(200.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OxygenBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                "(IMT)\nIndeks Massa Tubuh",
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                String.format("%.1f", result.value),
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = result.illustration),
                    contentDescription = result.status,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    result.status,
                    color = result.color,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
fun BmrCard(bmr: Float, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, HeartRateGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Text(
                "(BMR)\nMetabolisme Basal",
                color = HeartRateGreen,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, color = OxygenBlue)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        String.format("%.0f", bmr),
                        color = Color.Black,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Kalori per Hari",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun TeeCard(tee: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(HeartRateGreen),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(id = R.drawable.fire),
            contentDescription = "Kalori",
            tint = Color.Unspecified,
            modifier = Modifier
                .padding(start = 12.dp, end = 8.dp)
        )
        Text(
            "(TEE) Kebutuhan Energi",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .weight(1f)
        )
        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxHeight()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${String.format("%,.0f", tee)} kalori",
                color = OxygenBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
fun MacroNutrientCard(macros: MacroNutrients) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OxygenBlue)
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)) {
            Text(
                "Kebutuhan Zat Gizi Makro",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MacroPieChart(
                        macros = macros,
                        modifier = Modifier.size(120.dp)
                    )

                    Spacer(Modifier.width(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MacroItem(value = macros.proteinGrams, label = "Protein", color = ProteinColor)
                        MacroItem(value = macros.carbsGrams, label = "Karbohidrat", color = CarbsColor)
                        MacroItem(value = macros.fatGrams, label = "Lemak", color = FatColor)
                    }
                }
            }
        }
    }
}

@Composable
fun MacroItem(value: Int, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            modifier = Modifier
                .width(4.dp)
                .height(32.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(12.dp))
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)) {
                    append("$value")
                }
                withStyle(style = SpanStyle(fontSize = 14.sp)) {
                    append("gr")
                }
            },
            modifier = Modifier.width(80.dp)
        )
        Text(label, color = Color.Gray, fontStyle = FontStyle.Italic)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IdealWeightCard(
    bmiResult: BmiResult,
    idealWeight: Float,
    heightCm: Int,
    isMale: Boolean
) {
    if (bmiResult.status == "NORMAL") {
        IdealWeightNormalCard()
    } else {
        IdealWeightCalculationCard(idealWeight, heightCm, isMale)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun IdealWeightNormalCard(
) {
    Card (
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HeartRateGreen)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Berat Badan Ideal",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    "Berat badan sudah ideal!",
                    color = Color.White.copy(alpha = 0.9f),
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                Text(
                    formatIdealWeightTimestamp(),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .height(80.dp)
                    .background(Color.White.copy(alpha = 0.5f))
            )

            Icon(
                painter = painterResource(id = R.drawable.bmi_ideal), // Pastikan ikon ini ada
                contentDescription = "Berat Badan Ideal",
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 16.dp)
                    .size(64.dp),
                tint = Color.White
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun IdealWeightCalculationCard(
    idealWeight: Float,
    heightCm: Int,
    isMale: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HeartRateGreen)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Berat Badan Ideal",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    "*Disarankan karena status gizi Anda tidak normal",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                Text(
                    formatIdealWeightTimestamp(),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold)) {
                                append(String.format("%.0f", idealWeight))
                            }
                            withStyle(style = SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)) {
                                append("kg")
                            }
                        },
                        color = OxygenBlue
                    )
                    Text(
                        "$heightCm cm | ${if (isMale) "Pria" else "Perempuan"}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MacroPieChart(macros: MacroNutrients, modifier: Modifier = Modifier) {
    val data = listOf(
        macros.carbsGrams.toFloat(),
        macros.proteinGrams.toFloat(),
        macros.fatGrams.toFloat()
    )
    val colors = listOf(CarbsColor, ProteinColor, FatColor)

    var selectedIndex by remember { mutableStateOf(-1) }

    val total = data.sum()
    val sweepAngles = data.map { 360 * it / total }

    val animatedScale by animateFloatAsState(
        targetValue = if (selectedIndex != -1) 1.1f else 1.0f,
        label = "pieChartScale"
    )

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val x = offset.x - size.width / 2f
                        val y = offset.y - size.height / 2f
                        var angle = Math.toDegrees(kotlin.math.atan2(y, x).toDouble()).toFloat()
                        if (angle < 0) {
                            angle += 360
                        }
                        angle = (angle + 90) % 360

                        var accumulatedAngle = 0f
                        selectedIndex = sweepAngles.indexOfFirst { sweepAngle ->
                            accumulatedAngle += sweepAngle
                            angle < accumulatedAngle
                        }

                        tryAwaitRelease()
                        selectedIndex = -1
                    }
                )
            }
    ) {
        scale(scale = animatedScale) {
            var startAngle = -90f

            sweepAngles.forEachIndexed { index, sweepAngle ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = if (index == selectedIndex) Offset(-10f, -10f) else Offset.Zero,
                    size = if (index == selectedIndex) size * 1.1f else size
                )
                startAngle += sweepAngle
            }
        }
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun NutritionStatusPreview() {
//    NutritionStatusScreen(navController = rememberNavController())
//}
