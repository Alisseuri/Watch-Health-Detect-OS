package com.chrisp.healthdetect.ui.nutrition

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chrisp.healthdetect.ui.theme.BackgroundGray
import com.chrisp.healthdetect.ui.util.AppBottomNavigation

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionStatusScreen(
    navController: NavController
) {
    val weightKg = 50f
    val heightCm = 160f
    val age = 25
    val isMale = false

    val bmiResult = calculateBmi(weightKg, heightCm)
    val bmr = calculateBmr(weightKg, heightCm, age, isMale)
    val tee = calculateTee(bmr)
    val macros = calculateMacros(tee, weightKg)
    val idealWeight = calculateIdealWeight(heightCm)

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = { AppBottomNavigation(navController = navController, currentRoute = "cekgizi") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { PageTitle() }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BmiCard(
                        result = bmiResult,
                        modifier = Modifier
                            .weight(1f)
                    )
                    BmrCard(
                        bmr = bmr,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }

            item { TeeCard(tee = tee) }

            item { MacroNutrientCard(macros = macros) }

            item { IdealWeightCard(
                bmiResult = bmiResult,
                idealWeight = idealWeight,
                heightCm = heightCm.toInt(),
                isMale = isMale
            ) }
        }
    }
}

@Composable
fun PageTitle() {
    Column {
        Text(
            "Cek Status Gizi",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("*")
                }
                append("kategori usia dewasa")
            },
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun NutritionStatuscreenPreview() {
    NutritionStatusScreen(navController = rememberNavController())
}
