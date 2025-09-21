package com.chrisp.healthdetect.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chrisp.healthdetect.ui.theme.BackgroundGray
import com.chrisp.healthdetect.ui.theme.DarkText
import com.chrisp.healthdetect.ui.theme.LightGrayText
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import com.chrisp.healthdetect.ui.util.AppBottomNavigation
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chrisp.healthdetect.model.FraminghamRequest
import com.chrisp.healthdetect.model.FraminghamResponse
import com.chrisp.healthdetect.model.NutritionRequest
import com.chrisp.healthdetect.model.NutritionResponse
import com.chrisp.healthdetect.network.ProfileApiService
import com.chrisp.healthdetect.repository.ProfileRepository
import com.chrisp.healthdetect.ui.profile.ProfileViewModel
import com.chrisp.healthdetect.ui.profile.ProfileViewModelFactory
import kotlin.math.roundToInt


@Composable
fun DashboardScreen(
    navController: NavController,
    heartRate: Int,
    lastUpdatedTimestamp: Long,
    oxygenLevel: String,
    onOxygenLevelChange: (String) -> Unit,
    onHeartRateCardClick: () -> Unit,
    onOxygenCardClick: () -> Unit,
    profileViewModel: ProfileViewModel
) {
    // Ambil hasil skor dari ViewModel secara reaktif.
    // Kita hanya perlu satu state, karena keduanya (ascvd & framingham) ada dalam satu response.
    val apiResult by profileViewModel.framinghamResult.collectAsState()

    // Ambil nama pengguna dari UI state di ViewModel
    val username = profileViewModel.uiState.name.takeIf { it.isNotBlank() } ?: "Guest"

    // Gunakan skor dari hasil API sesuai dengan struktur data class yang benar.
    // Default ke 0 jika data belum ada.
    val ascvdScore = apiResult?.ascvd?.ascvdScore?.roundToInt() ?: 0
    val framinghamScore = apiResult?.framingham?.riskScore?.roundToInt() ?: 0

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = { AppBottomNavigation(navController = navController, currentRoute = currentRoute) }
    ) { paddingValues ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ){
            item { GreetingHeader(username = username) }

            item {
                VitalsSection(
                    heartRate = heartRate,
                    oxygenLevel = oxygenLevel.toIntOrNull() ?: 0,
                    lastUpdateTimestamp = lastUpdatedTimestamp,
                    onHeartRateCardClick = onHeartRateCardClick,
                    onOxygenCardClick = onOxygenCardClick
                )
            }

            item {
                Text(
                    "Skor ASCVD",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 8.dp)
                )
                // Gunakan ascvdScore yang dinamis
                RiskScoreCard(score = ascvdScore, riskInfo = getAscvdRisk(ascvdScore))
            }

            item {
                Text(
                    text = "Skor FRAMINGHAM",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 8.dp)
                )
                // Gunakan framinghamScore yang dinamis
                RiskScoreCard(score = framinghamScore, riskInfo = getFraminghamRisk(framinghamScore))
            }
        }
    }
}

@Composable
fun GreetingHeader(username: String) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text("Selamat datang,", fontSize = 18.sp, color = Color.DarkGray)
            Spacer(Modifier.height(5.dp))
            Text(username, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = DarkText)
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun DashboardScreenPreview() {
//    // Untuk membuat Preview berfungsi, kita perlu membuat instance palsu (mock) dari ViewModel.
//    // Ini adalah praktik umum karena ViewModel sering memiliki dependensi (seperti Repository).
//    val mockViewModel: ProfileViewModel = viewModel(
//        factory = ProfileViewModelFactory(
//            ProfileRepository(
//                // Buat implementasi dummy dari ApiService untuk preview
//                object : ProfileApiService {
//                    override suspend fun submitFramingham(request: FraminghamRequest): FraminghamResponse {
//                        TODO("Not yet implemented")
//                    }
//
//                    override suspend fun submitNutrition(request: NutritionRequest): NutritionResponse {
//                        TODO("Not yet implemented")
//                    }
//
//                    override suspend fun getNutritionResult(userId: String): NutritionResponse {
//                        TODO("Not yet implemented")
//                    }
//                }
//            )
//        )
//    )
//    // Atur nama pengguna di ViewModel untuk preview
//    mockViewModel.onNameChange("Chris")
//
//    DashboardScreen(
//        navController = rememberNavController(),
//        heartRate = 90,
//        lastUpdatedTimestamp = System.currentTimeMillis(),
//        oxygenLevel = "98",
//        onOxygenLevelChange = {},
//        onHeartRateCardClick = {},
//        onOxygenCardClick = {},
//        profileViewModel = mockViewModel // Pass mock ViewModel ke preview
//    )
//}