package de.christian2003.feature.auth.ui.recovery

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation


/**
 * Visual transformation transforms a recovery code from "ABCD1234ABCD1234" to "ABCD-1234-ABCD-1234".
 */
class RecoveryCodeVisualTransformation: VisualTransformation {

    /**
     * Offset mapping for the transformation.
     */
    private val offsetMapping: OffsetMapping = RecoveryCodeOffsetMapping()


    /**
     * Performs the visual transformation for the specified text.
     *
     * @param text  Text to transform.
     * @return      Transformed text.
     */
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed: String = if (text.text.length >= 24) {
            text.text.substring(0..23)
        } else {
            text.text
        }

        val builder = StringBuilder()

        for (i: Int in trimmed.indices) {
            builder.append(trimmed[i])
            if (i % 4 == 3 && i != 23) {
                builder.append('-')
            }
        }

        val transformedText = TransformedText(
            text = AnnotatedString(builder.toString()),
            offsetMapping = offsetMapping
        )
        return transformedText
    }


    /**
     * Offset mapping for the visual transformation of recovery codes.
     */
    private class RecoveryCodeOffsetMapping: OffsetMapping {

        /**
         * Mapping from the original to the transformed text.
         *
         * @param offset    Original offset.
         * @return          Transformed offset.
         */
        override fun originalToTransformed(offset: Int): Int {
            return when {
                offset <= 3 -> offset
                offset <= 7 -> offset + 1
                offset <= 11 -> offset + 2
                offset <= 15 -> offset + 3
                offset <= 19 -> offset + 4
                offset <= 23 -> offset + 5
                else -> 29
            }
        }


        /**
         * Mapping from the transformed to the original text.
         *
         * @param offset    Transformed offset.
         * @return          Original offset.
         */
        override fun transformedToOriginal(offset: Int): Int {
            return when {
                offset <= 4 -> offset
                offset <= 9 -> offset - 1
                offset <= 14 -> offset - 2
                offset <= 19 -> offset - 3
                offset <= 24 -> offset - 4
                offset <= 29 -> offset - 5
                else -> 24
            }
        }

    }

}
