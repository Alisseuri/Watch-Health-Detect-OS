package com.chrisp.healthdetect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chrisp.healthdetect.ui.activity.ActivityScreen
import com.chrisp.healthdetect.ui.bmi.BmiDetailScreen
import com.chrisp.healthdetect.ui.dashboard.DashboardScreen
import com.chrisp.healthdetect.ui.heartrate.HeartRateDetailScreen
import com.chrisp.healthdetect.ui.nutrition.NutritionStatusScreen
import com.chrisp.healthdetect.ui.oxygen.OxygenDetailScreen
import com.chrisp.healthdetect.ui.profile.Gender
import com.chrisp.healthdetect.ui.profile.ProfileScreen
import com.chrisp.healthdetect.ui.theme.HealthdetectwearTheme

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startWearableListenerService()
        setContent {
            HealthdetectwearTheme {
                MainApp()
            }
        }
    }

    @Composable
    private fun MainApp() {
        val navController = rememberNavController()
        val context = LocalContext.current

        var heartRate by remember { mutableStateOf(85) }
        var lastUpdateTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
        var username by remember { mutableStateOf("Chris") }
        var oxygenLevel by remember { mutableStateOf("98") }

        DisposableEffect(context) {
            val heartRateReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == "HEART_RATE_UPDATE") {
                        val hrString = intent.getStringExtra("heart_rate") ?: "0"
                        heartRate = hrString.toIntOrNull() ?: 0
                        lastUpdateTimestamp = System.currentTimeMillis()
                        Log.d("MainActivityCompose", "Heart rate state updated: $heartRate")
                    }
                }
            }
            val heartRateFilter = IntentFilter("HEART_RATE_UPDATE")
            ContextCompat.registerReceiver(context, heartRateReceiver, heartRateFilter, ContextCompat.RECEIVER_NOT_EXPORTED)


            val exerciseSummaryReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == "EXERCISE_SUMMARY_UPDATE") {
                        val summary = intent.getStringExtra("summary") ?: "No summary"
                        Log.d("MainActivityCompose", "Summary received: $summary")
                        Toast.makeText(context, summary, Toast.LENGTH_LONG).show()
                    }
                }
            }
            val summaryFilter = IntentFilter("EXERCISE_SUMMARY_UPDATE")
            ContextCompat.registerReceiver(context, exerciseSummaryReceiver, summaryFilter, ContextCompat.RECEIVER_NOT_EXPORTED)


            onDispose {
                context.unregisterReceiver(heartRateReceiver)
                context.unregisterReceiver(exerciseSummaryReceiver)
            }
        }

        NavHost(navController = navController, startDestination = "dashboard") {
            composable("dashboard") {
                DashboardScreen(
                    navController = navController,
                    heartRate = heartRate,
                    lastUpdatedTimestamp = lastUpdateTimestamp,
                    username = username,
                    onUsernameChange = { newUsername -> username = newUsername },
                    oxygenLevel = oxygenLevel,
                    onOxygenLevelChange = { newOxygenLevel -> oxygenLevel = newOxygenLevel },
                    onHeartRateCardClick = {
                        val avg = 97; val min = 42; val max = 120
                        navController.navigate("heartRateDetail/$heartRate/$avg/$min/$max/$lastUpdateTimestamp")
                    },
                    onOxygenCardClick = {
                        navController.navigate("oxygenDetail/$oxygenLevel/$lastUpdateTimestamp")
                    }
                )
            }

            composable("profile") {
                ProfileScreen(navController = navController)
            }

            composable(
                route = "heartRateDetail/{currentBpm}/{avgBpm}/{minBpm}/{maxBpm}/{timestamp}",
                arguments = listOf(
                    navArgument("currentBpm") { type = NavType.IntType },
                    navArgument("avgBpm") { type = NavType.IntType },
                    navArgument("minBpm") { type = NavType.IntType },
                    navArgument("maxBpm") { type = NavType.IntType },
                    navArgument("timestamp") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val currentBpm = backStackEntry.arguments?.getInt("currentBpm") ?: 0
                val avgBpm = backStackEntry.arguments?.getInt("avgBpm") ?: 0
                val minBpm = backStackEntry.arguments?.getInt("minBpm") ?: 0
                val maxBpm = backStackEntry.arguments?.getInt("maxBpm") ?: 0
                val timestamp = backStackEntry.arguments?.getLong("timestamp") ?: 0L

                HeartRateDetailScreen(
                    currentBpm = currentBpm,
                    avgBpm = avgBpm,
                    minBpm = minBpm,
                    maxBpm = maxBpm,
                    lastUpdateTimestamp = timestamp,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "oxygenDetail/{currentSpo2}/{timestamp}",
                arguments = listOf(
                    navArgument("currentSpo2") { type = NavType.StringType },
                    navArgument("timestamp") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val currentSpo2 = backStackEntry.arguments?.getString("currentSpo2")?.toIntOrNull() ?: 0
                val timestamp = backStackEntry.arguments?.getLong("timestamp") ?: 0L

                OxygenDetailScreen(
                    currentSpo2 = currentSpo2,
                    lastUpdateTimestamp = timestamp,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("cekgizi") {
                NutritionStatusScreen(
                    navController = navController,
                    profileViewModel = viewModel()
                )
            }

            composable(
                route = "bmiDetail/{age}/{gender}/{height}/{weight}",
                arguments = listOf(
                    navArgument("age") {
                        type = NavType.IntType
                    },
                    navArgument("gender") {
                        type = NavType.StringType
                    },
                    navArgument("height") {
                        type = NavType.IntType
                    },
                    navArgument("weight") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val age = backStackEntry.arguments?.getInt("age") ?: 0
                val genderStr = backStackEntry.arguments?.getString("gender") ?: Gender.WANITA.name
                val gender = Gender.valueOf(genderStr)
                val height = backStackEntry.arguments?.getInt("height") ?: 0
                val weight = backStackEntry.arguments?.getInt("weight") ?: 0

                BmiDetailScreen(
                    age = age,
                    gender = gender,
                    heightCm = height,
                    weightKg = weight,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("aktivitas") {
                ActivityScreen(navController = navController)
            }
        }
    }

    private fun startWearableListenerService() {
        try {
            val serviceIntent = Intent(this, HeartRateWearableListenerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to start WearableListenerService: ${e.message}", e)
        }
    }
}