package com.chrisp.healthdetect.ui.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.theme.BackgroundGray
import com.chrisp.healthdetect.ui.theme.DarkText
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import com.chrisp.healthdetect.ui.util.AppBottomNavigation

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileDisplayScreen(
    navController: NavController,
    uiState: UserProfileData,
    onEditClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = { AppBottomNavigation(navController = navController, currentRoute = "profile") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                DisplayHeader(
                    name = uiState.name,
                    age = viewModel.getAge(),
                    gender = uiState.gender,
                    onEditClick = onEditClick
                )
            }

            item {
                DisplayHistory(
                    isSmoker = uiState.isSmoker,
                    hasDiabetes = uiState.hasDiabetes
                )
            }

            item {
                DisplayParameters(
                    totalCholesterol = uiState.totalCholesterol,
                    isTotalCholesterolNormal = viewModel.isTotalCholesterolNormal(),
                    hdlCholesterol = uiState.hdlCholesterol,
                    isHdlCholesterolNormal = viewModel.isHdlCholesterolNormal(),
                    systolicBp = uiState.systolicBp,
                    isSystolicBpNormal = viewModel.isSystolicBpNormal(),
                    oxygenSaturation = uiState.oxygenSaturation,
                    isOxygenSaturationNormal = viewModel.isOxygenSaturationNormal()
                )
            }

            item {
                DisplayNutrition(
                    height = uiState.height,
                    weight = uiState.weight
                )
            }

            item {
                DisplayActivityFactor(level = uiState.activityLevel)
            }

            item {
                DisplayStressLevel(level = uiState.stressLevel)
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun DisplayHeader(
    name: String,
    age: Int?,
    gender: Gender?,
    onEditClick: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .clip(RoundedCornerShape(20.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_background2),
            contentDescription = "Header Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = if (name.isNotBlank()) name else "Isi Nama Terlebih Dahulu",
                    color = if (name.isNotBlank()) DarkText else Color.Gray,
                    fontSize = 22.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${age?.toString() ?: "--"} Tahun | ${gender?.name?.lowercase()?.replaceFirstChar { it.titlecase() } ?: "-"}",
                    color = DarkText
                )
            }
            IconButton (onClick = onEditClick) {
                Icon(
                    painterResource(id = R.drawable.pencil3),
                    contentDescription = "Edit Profil",
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Composable
private fun DisplayHistory(isSmoker: YesNo, hasDiabetes: YesNo) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionTitle("Riwayat")
            HistoryDisplayItem(label = "Merokok", value = if (isSmoker == YesNo.YA) "ADA" else "TIDAK ADA")
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            HistoryDisplayItem(label = "Diabetes", value = if (hasDiabetes == YesNo.YA) "ADA" else "TIDAK ADA")
        }
    }
}

@Composable
private fun HistoryDisplayItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = Color.Gray
        )
        Text(
            value,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DisplayParameters(
    totalCholesterol: String, isTotalCholesterolNormal: Boolean?,
    hdlCholesterol: String, isHdlCholesterolNormal: Boolean?,
    systolicBp: String, isSystolicBpNormal: Boolean?,
    oxygenSaturation: String, isOxygenSaturationNormal: Boolean?
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
        containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionTitle("Parameter")

            ParameterDisplayItem(
                label = "Kolesterol Total",
                value = totalCholesterol,
                unit = "mg/dL",
                isNormal = isTotalCholesterolNormal
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            ParameterDisplayItem(
                label = "HDL Kolesterol",
                value = hdlCholesterol,
                unit = "mg/dL",
                isNormal = isHdlCholesterolNormal
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            ParameterDisplayItem(
                label = "Tekanan Darah Sistolik",
                value = systolicBp,
                unit = "mmHg",
                isNormal = isSystolicBpNormal)

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            ParameterDisplayItem(
                label = "Saturasi Oksigen",
                value = oxygenSaturation,
                unit = "%",
                isNormal = isOxygenSaturationNormal
            )
        }
    }
}

@Composable
private fun ParameterDisplayItem(
    label: String,
    value: String,
    unit: String,
    isNormal: Boolean?) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = if (value.isNotBlank()) value else "---",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(4.dp))

                Text(
                    text = unit,
                    color = Color.Gray
                )
            }
        }
        isNormal?.let {
            Icon(
                painter = painterResource(id = if (it) R.drawable.param_safe else R.drawable.param_alert),
                contentDescription = if (it) "Normal" else "Alert",
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NutritionDisplayItem(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            color = Color.Gray
        )

        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = if (value.isNotBlank()) value else "--",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = unit,
                modifier = Modifier
                    .padding(bottom = 6.dp)
            )
        }
    }
}

@Composable
private fun DisplayNutrition(
    height: String,
    weight: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Nutrisi")

            Row(modifier = Modifier.fillMaxWidth()) {
                NutritionDisplayItem(
                    label = "Tinggi Badan",
                    value = height,
                    unit = "cm",
                    modifier = Modifier.weight(1f)
                )

                NutritionDisplayItem(
                    label = "Berat Badan",
                    value = weight,
                    unit = "kg",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DisplayActivityFactor(level: ActivityLevel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)
        ) {
            SectionTitle("Faktor Aktivitas")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeartRateGreen.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                        .background(HeartRateGreen, CircleShape)
                        .padding(12.dp)
                    ) {
                        Text(
                            text = "${level.ordinal + 1}",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.width(16.dp))

                    Text(
                        level.displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeartRateGreen)
                }
            }
        }
    }
}

@Composable
private fun DisplayStressLevel(level: StressLevel) {
    val levelColor = when (level) {
        StressLevel.RINGAN -> Color(0xFF76C893)
        StressLevel.SEDANG -> Color(0xFFFFA73B)
        StressLevel.BERAT -> Color(0xFFE94D64)
    }
    Column {
        SectionTitle("Stress Level")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(levelColor, CircleShape)
            )
            Box(
                modifier = Modifier
                    .weight(4f)
                    .border(1.dp, levelColor, RoundedCornerShape(12.dp))
                    .padding(vertical = 12.dp)
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${level.displayName} (${level.value})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = levelColor
                )
            }
        }
    }
}