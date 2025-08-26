package com.chrisp.healthdetect.ui.bmi

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.profile.Gender
import com.chrisp.healthdetect.ui.theme.BackgroundGray
import com.chrisp.healthdetect.ui.theme.HeartRateGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiDetailScreen(
    age: Int,
    gender: Gender,
    heightCm: Int,
    weightKg: Int,
    onBackClick: () -> Unit
) {
    val interpretation = getBmiInterpretation(
        weightKg.toFloat(),
        heightCm.toFloat()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hasil Kalkulasi",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp
                    )
                },

                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painterResource(id = R.drawable.arrow_left),
                            contentDescription = "Kembali",
                            tint = HeartRateGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Skor IMT",
                color = Color.Gray,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            BmiResultCard(interpretation = interpretation)
            Spacer(modifier = Modifier.height(32.dp))

            UserDataSummary(
                age = age,
                gender = gender.name.replaceFirstChar { it.titlecase() },
                height = "$heightCm cm",
                weight = "$weightKg cm"
            )
            Spacer(modifier = Modifier.height(32.dp))

            NotesCard(interpretation = interpretation)


        }
    }
}

@Preview(showBackground = true)
@Composable
fun BmiDetailScreenPreview() {
    BmiDetailScreen(
        age = 25,
        gender = Gender.WANITA,
        heightCm = 160,
        weightKg = 50,
        onBackClick = {}
    )
}