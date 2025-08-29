package com.chrisp.healthdetect.ui.profile

import android.R.attr.level
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chrisp.healthdetect.ui.theme.BackgroundGray
import com.chrisp.healthdetect.ui.util.AppBottomNavigation
import java.time.LocalDate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chrisp.healthdetect.repository.ProfileRepository
import com.chrisp.healthdetect.network.ProfileApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.currentBackStackEntryAsState
import okhttp3.logging.HttpLoggingInterceptor


class ProfileViewModelFactory(private val repository: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    navController: NavController
) {
    // Create API service with proper configuration
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
            .baseUrl("http://10.0.2.2:5000/") // Use 10.0.2.2 for Android emulator to access localhost
            // For physical device, use your actual IP address like "http://192.168.1.100:5000/"
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ProfileApiService::class.java)
    }

    val repository = remember { ProfileRepository(apiService) }
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(repository))

    val uiState = profileViewModel.uiState
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (uiState.isEditMode) {
        ProfileInputScreen(
            navController = navController,
            uiState = uiState,
            viewModel = profileViewModel
        )
    } else {
        ProfileDisplayScreen(
            navController = navController,
            uiState = uiState,
            onEditClick = { profileViewModel.setEditMode(true) }
        )
    }
}


//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun ProfileScreen(
//    navController: NavController,
//
//    ) {
//    var username by remember { mutableStateOf("Chris ") }
//    var dob by remember { mutableStateOf<LocalDate?>(null) }
//    var selectedGender by remember { mutableStateOf<Gender?>(null) }
//    var isSmoker by remember { mutableStateOf(YesNo.TIDAK) }
//    var hasDiabetes by remember { mutableStateOf(YesNo.TIDAK) }
//
//    var totalCholesterol by remember { mutableStateOf("") }
//    var hdlCholesterol by remember { mutableStateOf("") }
//    var systolicBp by remember { mutableStateOf("") }
//    var oxygenSaturation by remember { mutableStateOf("") }
//
//    var height by remember { mutableStateOf("") }
//    var weight by remember { mutableStateOf("") }
//
//    var activityLevel by remember { mutableStateOf(ActivityLevel.BEDREST) }
//    var stressLevel by remember { mutableStateOf(StressLevel.BERAT) }
//
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    Scaffold(
//        containerColor = BackgroundGray,
//        bottomBar = { AppBottomNavigation(navController = navController, currentRoute = currentRoute) }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp),
//            contentPadding = PaddingValues(bottom = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(24.dp)
//        ) {
//            item { ProfileHeader(username = username) }
//
//            item {
//                Text(
//                    "Informasi Pribadi",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp,
//                    modifier = Modifier
//                        .padding(top = 16.dp, bottom = 8.dp)
//                )
//                PersonalInfoSection(
//                    dob = dob,
//                    onDobSelected = { dob = it },
//                    selectedGender = selectedGender,
//                    onGenderSelect = { selectedGender = it }
//                )
//            }
//
//            item {
//                Text(
//                    "Riwayat",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp,
//                    modifier = Modifier
//                        .padding(top = 24.dp, bottom = 8.dp)
//                )
//                HistorySection(
//                    isSmoker = isSmoker,
//                    onSmokerChange = { isSmoker = it },
//                    hasDiabetes = hasDiabetes,
//                    onDiabetesChange = { hasDiabetes = it }
//                )
//            }
//
//            item {
//                ParameterSection(
//                    totalCholesterol = totalCholesterol, onTotalCholesterolChange = { totalCholesterol = it },
//                    hdlCholesterol = hdlCholesterol, onHdlCholesterolChange = { hdlCholesterol = it },
//                    systolicBp = systolicBp, onSystolicBpChange = { systolicBp = it },
//                    oxygenSaturation = oxygenSaturation, onOxygenSaturationChange = { oxygenSaturation = it }
//                )
//            }
//
//            item {
//                NutritionSection(
//                    height = height, onHeightChange = { height = it },
//                    weight = weight, onWeightChange = { height = it }
//                )
//            }
//
//            item {
//                ActivityFactorSection(
//                    selectedLevel = activityLevel,
//                    onLevelSelected = { activityLevel = it }
//                )
//            }
//
//            item {
//                StressLevelSection (
//                    selectedLevel = stressLevel,
//                    onLevelSelected = { stressLevel = it }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun SectionTitle(title: String) {
//    Text(
//        text = title,
//        fontWeight = FontWeight.Bold,
//        fontSize = 20.sp,
//        modifier = Modifier.padding(bottom = 16.dp)
//    )
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun ProfileScreenPreview() {
//    ProfileScreen(navController = rememberNavController())
//}