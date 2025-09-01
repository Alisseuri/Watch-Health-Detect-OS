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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chrisp.healthdetect.ui.profile.Gender
import com.chrisp.healthdetect.ui.profile.ProfileViewModel
import com.chrisp.healthdetect.ui.profile.ProfileViewModelFactory
import com.chrisp.healthdetect.ui.theme.BackgroundGray
import com.chrisp.healthdetect.ui.util.AppBottomNavigation
import com.chrisp.healthdetect.repository.ProfileRepository
import com.chrisp.healthdetect.network.ProfileApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionStatusScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel? = null // Accept existing ViewModel as parameter
) {
    // Create ViewModel with proper factory if not provided
    val viewModel = profileViewModel ?: run {
        val apiService = remember {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ProfileApiService::class.java)
        }

        val repository = remember { ProfileRepository(apiService) }
        viewModel<ProfileViewModel>(factory = ProfileViewModelFactory(repository))
    }

    val uiState = viewModel.uiState

    val weightKg = uiState.weight.toFloatOrNull() ?: 0f
    val heightCm = uiState.height.toFloatOrNull() ?: 0f
    val age = viewModel.getAge() ?: 0
    val gender = uiState.gender ?: Gender.WANITA
    val isMale = uiState.gender == Gender.PRIA

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
                            .weight(1f),
                        onClick = {
                            if (heightCm > 0 && weightKg > 0 && age > 0) {
                                navController.navigate("bmiDetail/$age/${gender.name}/$heightCm/$weightKg")
                            }
                        }
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
    // Create a mock ProfileViewModel for preview
    val mockApiService = remember {
        val okHttpClient = OkHttpClient.Builder().build()
        Retrofit.Builder()
            .baseUrl("http://localhost:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ProfileApiService::class.java)
    }
    val mockRepository = remember { ProfileRepository(mockApiService) }
    val mockViewModel = viewModel<ProfileViewModel>(factory = ProfileViewModelFactory(mockRepository))

    NutritionStatusScreen(
        navController = rememberNavController(),
        profileViewModel = mockViewModel
    )
}