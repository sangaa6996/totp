package com.dev.totpkotlinn

import java.security.SecureRandom
import java.util.Random

class RandomGenerator(
    charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9'),
    var random: Random = SecureRandom()
) {

    init {
        require(charPool.isNotEmpty()) { "Char pool must not be empty." }
    }

    var charPool = charPool
        set(value) {
            require(value.isNotEmpty()) { "Char pool must not be empty." }
            field = value
        }

    /**
     * Creates a string with random characters from the character pool.
     *
     * @param length the length of the string.
     * @throws IllegalArgumentException if the length is negative.
     * @return the generated string.
     */
    fun generateRandomStringFromCharPool(length: Int): String {
        require(length >= 0) { "Length must >= 0." }
        return (1..length)
            .map { random.nextInt(charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}