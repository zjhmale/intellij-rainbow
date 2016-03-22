package zjhmale.rainbow.encode

import java.security.MessageDigest

/**
 * Created by zjh on 16/3/22.
 */
object HashFace {
    private val ALGORITHM = "MD5"

    private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    /**
     * encode string

     * @param algorithm
     * *
     * @param str
     * *
     * @return String
     */
    fun encode(algorithm: String, str: String?): String? {
        if (str == null) {
            return null
        }
        try {
            val messageDigest = MessageDigest.getInstance(algorithm)
            messageDigest.update(str.toByteArray())
            return getFormattedText(messageDigest.digest())
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    /**
     * encode By MD5

     * @param str
     * *
     * @return String
     */
    fun encodeByMD5(str: String?): String? {
        if (str == null) {
            return null
        }
        try {
            val messageDigest = MessageDigest.getInstance(ALGORITHM)
            messageDigest.update(str.toByteArray())
            return getFormattedText(messageDigest.digest())
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    /**
     * Takes the raw bytes from the digest and formats them correct.

     * @param bytes the raw bytes from the digest.
     * *
     * @return the formatted bytes.
     */
    private fun getFormattedText(bytes: ByteArray): String {
        val len = bytes.size
        val buf = StringBuilder(len * 2)
        // 把密文转换成十六进制的字符串形式
        for (j in 0..len - 1) {
            val b = bytes[j] as Int
            buf.append(HEX_DIGITS[b shl 4 and 0x0f])
            buf.append(HEX_DIGITS[b and 0x0f])
        }
        return buf.toString()
    }

    fun rainbowIdentifierHashBytesToUse(): Int {
        return Math.ceil(Logarithm.log(Integer.MAX_VALUE.toDouble(), 2.0) / 8.0).toInt()
    }

    fun rainbowIdentifierHash(identifier: String): Int {
        val hash = HashFace.encode("SHA1", identifier) ?: return 0
        val len = hash.length
        var i = len - rainbowIdentifierHashBytesToUse()
        var result = 0
        while (i < len) {
            result = result * 256 + hash.get(i).toInt()
            i += 1
        }
        return result
    }
}