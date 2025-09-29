package com.chrisp.healthdetect.ui.steps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonBorder
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.chrisp.healthdetect.HeartRateService
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.heartrate.TimerState
import com.chrisp.healthdetect.utils.LottieAnimationPlayer

@Composable
fun StepsScreen(viewModel: StepsViewModel = viewModel()) {
    val context = LocalContext.current

    var hasPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context,
        Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) }
    val permissionLauncher = rememberLauncherForActivityResult (
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasPermission = isGranted }
    )

    if (hasPermission) {
        val timerState = viewModel.timerState
        val result = viewModel.result

        when (timerState) {
            TimerState.STOPPED -> InitialStepsScreen (onStartClick = { viewModel.startTimer(context) })
            TimerState.RUNNING, TimerState.PAUSED -> StepsCountingScreen(viewModel = viewModel)
            TimerState.FINISHED -> {
                if (result != null) {
                    StepsResultScreen(
                        result = result,
                        onFinish = { viewModel.finishSessionAndReset() }
                    )
                } else {
                    InitialStepsScreen(
                        onStartClick = { viewModel.startTimer(context) }
                    )
                }
            }
        }
    } else {
        RequestPermissionScreen { permissionLauncher.launch(Manifest.permission.BODY_SENSORS) }
    }
}

@Composable
private fun InitialStepsScreen(
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        LottieAnimationPlayer(
            animationRes = R.raw.steps,
            modifier = Modifier.size(100.dp)
        )
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = ButtonDefaults.outlinedButtonBorder(borderColor = Color(0xFF00BCD4))
        ) {
            Text(
                "Start Activity",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StepsCountingScreen(
    viewModel: StepsViewModel
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        LottieAnimationPlayer(
            animationRes = R.raw.steps,
            modifier = Modifier
                .size(60.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetricDisplay(value = "${viewModel.heartRate}", unit = "BPM")
            MetricDisplay(value = "${viewModel.stepCount}", unit = "Steps")
        }
//        Divider(modifier = Modifier.width(100.dp).padding(vertical = 8.dp), color = Color.Gray)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                viewModel.formatTime(viewModel.elapsedTime),
                fontSize = 24.sp
            )

            ControlButton(
                iconRes = R.drawable.icon_stop,
                onClick = { viewModel.stopTimer(context) }
            )

            if (viewModel.timerState == TimerState.RUNNING) {
                ControlButton(
                    iconRes = R.drawable.icon_pause,
                    onClick = { viewModel.pauseTimer() }
                )
            } else {
                ControlButton(
                    iconRes = R.drawable.icon_resume,
                    onClick = { viewModel.startTimer(context) }
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MetricDisplay(
    value: String,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = unit,
            color = Color.Gray
        )
    }
}

@Composable
private fun ControlButton(
    iconRes: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF333333))
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.White
        )
    }
}