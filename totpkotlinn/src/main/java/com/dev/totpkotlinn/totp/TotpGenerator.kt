package com.dev.totpkotlinn.totp

import com.dev.totpkotlinn.HashAlgorithm
import dev.robinohs.totpkt.otp.OtpGenerator
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Base64
import java.nio.ByteBuffer
import java.time.Clock
import java.time.Duration
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.math.pow

class TotpGenerator(
    var algorithm: HashAlgorithm = HashAlgorithm.SHA1,
    codeLength: Int = 6,
    timePeriod: Duration = Duration.ofSeconds(60),
    tolerance: Int = 1,
    var clock: Clock = Clock.systemUTC()
) : OtpGenerator {
    init {
        require(codeLength >= 0) { "Code length must be >= 0." }
        require(timePeriod.toMillis() >= 1) { "Time period must be be >= 1." }
        require(tolerance >= 0) { "Tolerance must be be >= 0." }
    }

    var codeLength = codeLength
        set(value) {
            require(value >= 0) { "Code length must be >= 0." }
            field = value
        }

    var timePeriod = timePeriod
        set(value) {
            require(value.toMillis() >= 1) { "Time period must be be >= 1." }
            field = value
        }

    var tolerance = tolerance
        set(value) {
            require(value >= 0) { "Tolerance must be be >= 0." }
            field = value
        }

    override fun generateCode(secret: ByteArray, counter: Long): String {
        val currentCounter = computeCounterForMillis(counter)
        val payload: ByteArray = ByteBuffer.allocate(8).putLong(0, currentCounter).array()
        val hash = generateHash(secret, payload)
        val truncatedHash = truncateHash(hash)
        // generate code by computing the hash as integer mod 1000000
        val code = ByteBuffer.wrap(truncatedHash).int % 10.0.pow(codeLength).toInt()
        // pad code to correct length, could be too small
        return code.toString().padStart(codeLength, '0')
    }

    override fun isCodeValid(secret: ByteArray, counter: Long, givenCode: String): Boolean {
        val code = generateCode(secret, counter)
        return code == givenCode
    }

    private fun generateHash(secret: ByteArray, payload: ByteArray): ByteArray {
        val key = Base64().decode(secret)
        val mac = algorithm.getMacInstance()
        mac.init(SecretKeySpec(key, "RAW"))
        return mac.doFinal(payload)
    }

    private fun truncateHash(hash: ByteArray): ByteArray {
        // last nibble of hash
        val offset = hash.last().and(0x0F).toInt()
        // get 4 bytes of the hash starting at the offset
        val truncatedHash = ByteArray(4)
        for (i in 0..3) {
            truncatedHash[i] = hash[offset + i]
        }
        // remove most significant bit
        truncatedHash[0] = truncatedHash[0].and(0x7F)
        return truncatedHash
    }

    fun isCodeValidWithTolerance(secret: ByteArray, millis: Long, givenCode: String): Boolean {
        val validToken = getCodesInInterval(secret, millis)
        return givenCode in validToken
    }

    private fun getCodesInInterval(secret: ByteArray, start: Long): Set<String> {
        val validTokens = mutableSetOf<String>()
        validTokens.add(generateCode(secret, start))
        var currentTime = start - timePeriod.toMillis()
        repeat(tolerance) {
            validTokens.add(generateCode(secret, currentTime))
            currentTime -= timePeriod.toMillis()
        }
        return validTokens
    }

    private fun computeCounterForMillis(millis: Long): Long = Math.floorDiv(millis, timePeriod.toMillis())

    fun calculateTimeslotBeginning(millis: Long): Long {
        val counter = computeCounterForMillis(millis)
        return timePeriod.toMillis() * counter
    }

    fun calculateRemainingTime(millis: Long): Duration {
        val beginning = calculateTimeslotBeginning(millis)
        val end = beginning + timePeriod.toMillis()
        return Duration.ofMillis(end - millis)
    }
}