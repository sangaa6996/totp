package com.dev.totp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.totpkotlinn.SecretGenerator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException


class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()
    val timerVm = viewModels<TimerVm>();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "Main", builder = {
                composable("Main"){
                    GetOtpScreen(navController = navController)
                }
                composable("OtpScreen/{secretKey}"){
                    val secretKey = it.arguments?.getString("secretKey")
                    if (secretKey != null) {
                        OtpScreen(secretKey,timerVm.value)
                    };
                }
            })
        }
    }
    val secret = SecretGenerator().generateSecret()

    @Composable
    fun GetOtpScreen(navController: NavController) {
        var confirmTOTP by remember{ mutableStateOf("") }
        var responseMessage by remember{ mutableStateOf("") }
        val timerState = timerVm.value.timerStateFlow.collectAsState()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center){
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    content = { Text(text = "Get TOTP")},
                    onClick = {
                        timerVm.value.toggleStart(secret.secretAsByteArray)
                        navController.navigate("OtpScreen/${secret.secretAsString}")
                    }
                )
            }
            Text(text = responseMessage)
            TextField(value = confirmTOTP, onValueChange = {
                confirmTOTP = it;
            })
            Button(onClick = {
                val client = OkHttpClient().newBuilder()
                    .build()
                val mediaType = MediaType.parse("application/json")
                val body = RequestBody.create(
                    mediaType,
                    "{\r\n    \"secret\":\"${secret.secretAsString}\",\r\n    \"TOTP\":\"${confirmTOTP}\"\r\n}"
                )
                val request: Request = Request.Builder()
                    .url("http://192.168.1.9:3000/user/verify")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build()
                val response = client.newCall(request).enqueue(object :Callback{
                    override fun onFailure(call: Call, e: IOException) {}
                    override fun onResponse(call: Call, response: Response) {
                        responseMessage = (response.body()?.string()!!)
                    }
                })
            }) {
                Text(text = "Submit")
            }
        }
    }
}

