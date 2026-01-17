package de.christian2003.auth.models.recoverycodes


/**
 * Formatter for recovery codes.
 */
class RecoveryCodesFormatter {

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

}
