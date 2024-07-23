package com.dev.totpkotlinn

data class Base32Secret(val secretAsString: String, val secretAsByteArray: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Base32Secret

        if (secretAsString != other.secretAsString) return false
        if (!secretAsByteArray.contentEquals(other.secretAsByteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = secretAsString.hashCode()
        result = 31 * result + secretAsByteArray.contentHashCode()
        return result
    }
}