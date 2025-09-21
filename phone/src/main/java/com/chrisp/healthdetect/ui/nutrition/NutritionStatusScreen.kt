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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.profile.ProfileViewModel
import com.chrisp.healthdetect.ui.profile.ProfileViewModelFactory
import com.chrisp.healthdetect.ui.theme.BackgroundGray
import com.chrisp.healthdetect.ui.util.AppBottomNavigation
import com.chrisp.healthdetect.repository.ProfileRepository
import com.chrisp.healthdetect.network.ProfileApiService
import com.chrisp.healthdetect.ui.theme.BmiGemuk
import com.chrisp.healthdetect.ui.theme.BmiKurus
import com.chrisp.healthdetect.ui.theme.BmiNormal
import com.chrisp.healthdetect.ui.theme.BmiObesitas
import com.chrisp.healthdetect.ui.theme.BmiSangatKurus
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Add missing data classes
data class BmiResult(
    val value: Float,
    val status: String,
    val color: Color,
    val illustration: Int
)

data class MacroNutrients(
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionStatusScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val nutritionResult by profileViewModel.nutritionResult.collectAsState()
    val framinghamResult by profileViewModel.framinghamResult.collectAsState()
    val loading by profileViewModel.loading.collectAsState()
    val error by profileViewModel.error.collectAsState()

    // Get user ID from ProfileViewModel instead of hardcoding
    LaunchedEffect(Unit) {
        val userId = profileViewModel.getCurrentUserId()
        android.util.Log.d("NutritionScreen", "LaunchedEffect - userId: $userId")
        android.util.Log.d("NutritionScreen", "hasNutrition: ${nutritionResult != null}")
        android.util.Log.d("NutritionScreen", "hasFramingham: ${framinghamResult != null}")

        // Only fetch if we have a user ID but no nutrition data
        if (userId != null && nutritionResult == null) {
            android.util.Log.d("NutritionScreen", "Fetching nutrition data for userId: $userId")
            profileViewModel.getNutritionResult(userId)
        } else if (userId == null) {
            android.util.Log.d("NutritionScreen", "No user ID available - user needs to complete profile first")
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = { AppBottomNavigation(navController = navController, currentRoute = "cekgizi") }
    ) { paddingValues ->
        when {
            loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text("Loading nutrition data...", modifier = Modifier.padding(top = 16.dp))
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Error: $error",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Please complete your profile first by going to the Profile tab",
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            nutritionResult != null -> {
                val nutrition = nutritionResult!!
                val user = nutrition.user
                val result = nutrition.result

                android.util.Log.d("NutritionScreen", "Displaying nutrition data for user: ${user.name}")
                android.util.Log.d("NutritionScreen", "BMI: ${result.bmi}, Category: ${result.bmiCategory}")

                // Convert API BMI data to BmiResult format
                val bmiResult = BmiResult(
                    value = result.bmi,
                    status = result.bmiCategory.uppercase(),
                    color = getBmiColor(result.bmi),
                    illustration = getBmiIllustration(result.bmi)
                )

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
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    // Navigate with API data
                                    val heightInt = nutrition.nutritionData.height.toInt()
                                    val weightInt = nutrition.nutritionData.weight.toInt()
                                    navController.navigate("bmiDetail/${user.age}/${if (user.gender == "male") "PRIA" else "WANITA"}/$heightInt/$weightInt")
                                }
                            )
                            BmrCard(
                                bmr = result.bmr,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item { TeeCard(tee = result.tee) }

                    item {
                        // Convert API macro data to MacroNutrients format
                        val macros = MacroNutrients(
                            proteinGrams = result.proteinGram.toInt(),
                            carbsGrams = result.carbGram.toInt(),
                            fatGrams = result.fatGram.toInt()
                        )
                        MacroNutrientCard(macros = macros)
                    }

                    item {
                        IdealWeightCard(
                            bmiResult = bmiResult,
                            idealWeight = result.idealWeight,
                            heightCm = nutrition.nutritionData.height.toInt(),
                            isMale = user.gender == "male"
                        )
                    }
                }
            }
            else -> {
                // Check if user has completed profile but nutrition data failed to load
                val hasProfile = profileViewModel.hasCompletedProfile()
                android.util.Log.d("NutritionScreen", "No nutrition data, hasProfile: $hasProfile")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (hasProfile) {
                        Text(
                            "Unable to load nutrition data",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Please try again or check your connection",
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    } else {
                        Text(
                            "No profile data found",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Please complete your profile first by going to the Profile tab",
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// Helper functions to convert API BMI values to UI format
private fun getBmiColor(bmi: Float): Color {
    return when {
        bmi < 17.0 -> BmiSangatKurus
        bmi < 18.5 -> BmiKurus
        bmi < 25.0 -> BmiNormal
        bmi < 27.0 -> BmiGemuk
        else -> BmiObesitas
    }
}

private fun getBmiIllustration(bmi: Float): Int {
    return when {
        bmi < 17.0 -> R.drawable.bmi_severe_thin
        bmi < 18.5 -> R.drawable.bmi_moderate_thin
        bmi < 25.0 -> R.drawable.bmi_normal
        bmi < 27.0 -> R.drawable.bmi_overweight
        else -> R.drawable.bmi_obesity
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
fun NutritionStatusScreenPreview() {
    // This preview won't work perfectly since we need actual ViewModel data
    val mockNavController = rememberNavController()

    // For preview, you'd need to create a mock ViewModel with sample data
    // This is just a placeholder to prevent compile errors
    NutritionStatusScreen(
        navController = mockNavController,
        profileViewModel = viewModel() // This won't work in preview without proper setup
    )
}