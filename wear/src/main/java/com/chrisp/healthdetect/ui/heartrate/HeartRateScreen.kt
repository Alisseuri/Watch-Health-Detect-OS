package com.chrisp.healthdetect.ui.heartrate

import android.Manifest
import android.content.Context
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.utils.LottieAnimationPlayer

@Composable
fun HeartRateScreenWithPermission() {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
        }
    )

    if (hasPermission) {
        HeartRateScreen()
    } else {
        RequestPermissionScreen {
            permissionLauncher.launch(Manifest.permission.BODY_SENSORS)
        }
    }
}

@Composable
fun HeartRateScreen(viewModel: HeartRateViewModel = viewModel()) {
    val timerState = viewModel.timerState
    val result = viewModel.result

    when (timerState) {
        TimerState.STOPPED -> InitialScreen(onStartClick = { viewModel.startTimer() })
        TimerState.RUNNING, TimerState.PAUSED -> CountingScreen(viewModel = viewModel)
        TimerState.FINISHED -> {
            if (result != null) {
                HeartRateResultScreen(
                    result = result,
                    onFinish = { viewModel.finishSession() } // Tombol Selesai akan me-reset state
                )
            } else {
                InitialScreen(onStartClick = { viewModel.startTimer() })
            }
        }
    }
}


@Composable
private fun InitialScreen(
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier
            .weight(1f))
        LottieAnimationPlayer(animationRes = R.raw.heart2, modifier = Modifier.size(100.dp))
        Spacer(modifier = Modifier.height(24.dp))

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
            border = ButtonDefaults.outlinedButtonBorder(borderColor = Color(0xFFFE1D69))
        ) {
            Text("Start Count", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun CountingScreen(viewModel: HeartRateViewModel) {
    val heartRate = viewModel.heartRate
    val elapsedTime = viewModel.elapsedTime
    val timerState = viewModel.timerState

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        LottieAnimationPlayer(animationRes = R.raw.heart2, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBEB73))) {
                    append("$heartRate")
                }
                withStyle(style = SpanStyle(fontSize = 24.sp)) {
                    append(" BPM")
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(viewModel.formatTime(elapsedTime), fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))

            ControlButton(
                iconRes = R.drawable.icon_stop,
                onClick = { viewModel.stopTimer() }
            )
            Spacer(modifier = Modifier.width(8.dp))

            if (timerState == TimerState.RUNNING) {
                ControlButton(
                    iconRes = R.drawable.icon_pause,
                    onClick = { viewModel.pauseTimer() }
                )
            } else {
                ControlButton(
                    iconRes = R.drawable.icon_resume,
                    onClick = { viewModel.startTimer() }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ControlButton(iconRes: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF333333))
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null, tint = Color.White
        )
    }
}


@Composable
private fun RequestPermissionScreen(onPermissionRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Izin sensor tubuh diperlukan...", textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onPermissionRequest) {
            Text("Berikan Izin")
        }
    }
}