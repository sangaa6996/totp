package com.dev.totp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.totpkotlinn.Base32Secret
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerVm : ViewModel() {
    private val timerIntent = TimerUseCase(viewModelScope)
    val timerStateFlow: StateFlow<TimerState> = timerIntent.timerStateFlow
    fun toggleStart(secretKey:ByteArray) = timerIntent.toggleTime(secretKey)
}

data class TimerState(
    val secondsRemaining: Int? = null,
    val totalSeconds: Int = 60,
    val textWhenStopped: String = "-",
    val OTP: String = ""
) {
    val displaySeconds: String = (secondsRemaining ?: textWhenStopped).toString()

    // Show 100% if seconds remaining is null
    val progressPercentage: Float = (secondsRemaining ?: totalSeconds) / totalSeconds.toFloat()

    override fun toString(): String = "Seconds Remaining $secondsRemaining, totalSeconds: $totalSeconds, progress: $progressPercentage"

}