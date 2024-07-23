package com.dev.totp

import com.dev.totpkotlinn.SecretGenerator
import com.dev.totpkotlinn.totp.TotpGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.time.Instant

class TimerUseCase(private val timerScope: CoroutineScope) {

    private var _timerStateFlow = MutableStateFlow(TimerState())
    val timerStateFlow: StateFlow<TimerState> = _timerStateFlow
    val totpGenerator = TotpGenerator();

    private var job: Job? = null

    fun toggleTime(secretKey: ByteArray) {
        if(job == null){
            job = timerScope.launch {
                val remaining = totpGenerator.calculateRemainingTime(Instant.now().toEpochMilli())
                val code = totpGenerator.generateCode(
                    secretKey,
                    Instant.now().toEpochMilli()
                );
                try {
                    initTimer(remaining.seconds.toInt(), code)
                        .collect {
                            _timerStateFlow.emit(it);
                        }
                }
                finally {
                    delay(1000);
                    job = null;
                    toggleTime(secretKey)
                }
            }
        }
    }
    /**
     * The timer emits the total seconds immediately.
     * Each second after that, it will emit the next value.
     */
    private fun initTimer(totalSeconds: Int, TOTP: String): Flow<TimerState> =
//        generateSequence(totalSeconds - 1 ) { it - 1 }.asFlow()
        (totalSeconds - 1 downTo 0).asFlow() // Emit total - 1 because the first was emitted onStart
            .onEach { delay(1000) } // Each second later emit a number
            .onStart { emit(totalSeconds) } // Emit total seconds immediately
            .conflate() // In case the operation onTick takes some time, conflate keeps the time ticking separately
            .transform { remainingSeconds: Int ->
                emit(TimerState(remainingSeconds, OTP = TOTP))
            }
}