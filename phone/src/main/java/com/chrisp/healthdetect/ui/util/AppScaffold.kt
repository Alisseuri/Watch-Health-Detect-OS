package com.chrisp.healthdetect.ui.util

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import com.chrisp.healthdetect.ui.theme.LightGrayText

@Composable
fun AppBottomNavigation(
    navController: NavController,
    currentRoute: String?
) {
    val items = listOf(
        Screen.Dashboard,
        Screen.Aktivitas,
        Screen.CekGizi,
        Screen.Profil
    )

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        tonalElevation = 8.dp
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = if (currentRoute == screen.route) screen.selectedIcon else screen.icon),
                        contentDescription = screen.title,
                        tint = Color.Unspecified
                    )
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {

                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = HeartRateGreen,
                    unselectedTextColor = LightGrayText,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

sealed class Screen(
    val route: String,
    val title: String,
    val icon: Int,
    val selectedIcon: Int
) {
    object Dashboard : Screen("dashboard", "Dashboard", R.drawable.dashboard, R.drawable.dashboard_clicked)
    object Profil : Screen("profile", "Profil", R.drawable.profile, R.drawable.profile_clicked)
    object Aktivitas : Screen("aktivitas", "Aktivitas", R.drawable.activity, R.drawable.activity_clicked)
    object CekGizi : Screen("cekgizi", "Cek Gizi", R.drawable.ion_nutrition, R.drawable.ion_nutrition_clicked)
}
