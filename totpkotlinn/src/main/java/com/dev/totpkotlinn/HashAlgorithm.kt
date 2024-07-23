package com.dev.totpkotlinn

import javax.crypto.Mac

enum class HashAlgorithm(private val algName: String, val keySize: Int) {
    SHA1("HmacSHA1", 20),
    SHA256("HmacSHA256", 32),
    SHA512("HmacSHA512", 64);

    fun getMacInstance(): Mac = Mac.getInstance(algName)
}