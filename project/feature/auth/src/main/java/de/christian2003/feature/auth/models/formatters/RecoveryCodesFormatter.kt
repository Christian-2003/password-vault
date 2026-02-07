package de.christian2003.feature.auth.models.formatters


/**
 * Formatter for recovery codes.
 */
internal class RecoveryCodesFormatter {

    /**
     * Formats a Base32 recovery code into the following pattern:
     * XXXX-XXXX-XXXX-XXXX-XXXX-XXXX
     *
     * @param code  Recovery code to format.
     * @return      Formatted recovery code.
     */
    fun format(code: CharArray): String {
        val builder = StringBuilder()

        for (i: Int in code.indices) {
            builder.append(code[i])
            if ((i + 1) % 4 == 0 && i != code.lastIndex) {
                builder.append("-\u200B") //u200B is a zero-width break opportunity
            }
        }

        return builder.toString()
    }


    /**
     * Converts the specified Base32-formatted recovery code into a char array which contains only
     * data and no formatting characters.
     *
     * @param code  Base32-formatted recovery code to convert to a char array.
     * @return      Converted char array.
     */
    fun convertBack(code: String): CharArray {
        val codeWithoutDashes: String = code.replace("-", "").uppercase()
        val result = CharArray(codeWithoutDashes.length)

        for (i: Int in result.indices) {
            result[i] = codeWithoutDashes[i]
        }

        return result
    }

}
