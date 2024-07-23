package com.dev.totp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TimerDisplay(timerState: TimerState, toggleStartStop: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Switch(checked = , onCheckedChange = )
        Row{
            Text(text = timerState.OTP)
        }
        Row {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    timerState.progressPercentage,
                    Modifier.clickable {
                        toggleStartStop()
                    })

                Text(timerState.displaySeconds)
            }
        }
    }
}