//package com.chrisp.healthdetect.ui.steps
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.wear.compose.material.Button
//import androidx.wear.compose.material.ButtonDefaults
//import androidx.wear.compose.material.Icon
//import androidx.wear.compose.material.Text
//import com.chrisp.healthdetect.R
//import com.chrisp.healthdetect.ui.heartrate.TimerState
//import com.chrisp.healthdetect.utils.LottieAnimationPlayer
//
//@Composable
//fun StepsScreen(viewModel: StepsViewModel = viewModel()) {
//    val timerState = viewModel.timerState
//    val result = viewModel.result
//
//    when (timerState) {
//        TimerState.STOPPED -> InitialStepsScreen(onStartClick = { viewModel.startTimer() })
//        TimerState.RUNNING, TimerState.PAUSED -> StepsCountingScreen(viewModel = viewModel)
//        TimerState.FINISHED -> {
//            if (result != null) {
//                StepsResultScreen(
//                    result = result,
//                    onFinish = { viewModel.finishSession() }
//                )
//            } else {
//                InitialStepsScreen(onStartClick = { viewModel.startTimer() })
//            }
//        }
//    }
//}
//
//@Composable
//private fun InitialStepsScreen(
//    onStartClick: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Spacer(modifier = Modifier
//            .weight(1f))
//        LottieAnimationPlayer(
//            animationRes = R.raw.steps,
//            modifier = Modifier
//                .size(100.dp)
//        )
//        Spacer(modifier = Modifier.height(24.dp))
//        Button(
//            onClick = onStartClick,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(52.dp),
//            shape = RoundedCornerShape(16.dp),
//            colors = ButtonDefaults.buttonColors(
//                backgroundColor = Color.Transparent,
//                contentColor = Color.White
//            ),
//            border = ButtonDefaults.outlinedButtonBorder(borderColor = Color(0xFF11E1F5))
//        ) {
//            Text(
//                "Start Count",
//                fontWeight = FontWeight.Bold,
//                fontSize = 18.sp
//            )
//        }
//        Spacer(modifier = Modifier.weight(1f))
//    }
//}
//
//@Composable
//private fun StepsCountingScreen(viewModel: StepsViewModel) {
//    val stepCount = viewModel.stepCount
//    val elapsedTime = viewModel.elapsedTime
//    val timerState = viewModel.timerState
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Spacer(modifier = Modifier.weight(0.5f))
//        LottieAnimationPlayer(
//            animationRes = R.raw.steps,
//            modifier = Modifier.size(80.dp)
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(
//            buildAnnotatedString {
//                withStyle(
//                    style = SpanStyle(
//                        fontSize = 48.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF11E1F5)
//                    )
//                ) {
//                    append("$stepCount")
//                }
//                withStyle(
//                    style = SpanStyle(
//                        fontSize = 24.sp,
//                        color = Color.White
//                    )
//                ) {
//                    append(" steps")
//                }
//            }
//        )
////        Divider(modifier = Modifier.width(100.dp).padding(vertical = 8.dp), color = Color.Gray)
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text(viewModel.formatTime(elapsedTime), fontSize = 24.sp)
//            Spacer(modifier = Modifier.width(16.dp))
//            ControlButton(iconRes = R.drawable.icon_stop, onClick = { viewModel.stopTimer() })
//            Spacer(modifier = Modifier.width(8.dp))
//            if (timerState == TimerState.RUNNING) {
//                ControlButton(iconRes = R.drawable.icon_pause, onClick = { viewModel.pauseTimer() })
//            } else {
//                ControlButton(iconRes = R.drawable.icon_resume, onClick = { viewModel.startTimer() })
//            }
//        }
//        Spacer(modifier = Modifier.weight(1f))
//    }
//}
//
//@Composable
//private fun ControlButton(iconRes: Int, onClick: () -> Unit) {
//    Button(
//        onClick = onClick,
//        modifier = Modifier.size(48.dp),
//        shape = CircleShape,
//        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF333333))
//    ) {
//        Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = Color.White)
//    }
//}