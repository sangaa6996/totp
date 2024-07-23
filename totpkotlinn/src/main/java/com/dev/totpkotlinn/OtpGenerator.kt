package dev.robinohs.totpkt.otp

interface OtpGenerator {
    fun generateCode(secret: ByteArray, counter: Long): String
    fun isCodeValid(secret: ByteArray, counter: Long, givenCode: String): Boolean
}