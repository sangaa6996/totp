package com.dev.totp

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.dev.totp.ui.theme.MyApplicationTheme
import com.dev.totpkotlinn.Base32Secret
import com.dev.totpkotlinn.SecretGenerator

@Composable
fun OtpScreen(secretKey: String, vm: TimerVm) {
        // A surface container using the 'background' color from the theme
    val timerState = vm.timerStateFlow.collectAsState();
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment =  Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimerDisplay(timerState = timerState.value){
            println(timerState.value.OTP);
            println(secretKey);
        }
    }
}
