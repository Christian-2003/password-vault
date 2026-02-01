package de.christian2003.security.application.usecases

import de.christian2003.security.application.services.RecoveryCodeEncoderService
import kotlinx.coroutines.coroutineScope
import java.security.SecureRandom
import javax.inject.Inject


/**
 * Use case to generate recovery codes.
 *
 * @param recoveryCodeEncoderService    Service to encode recovery codes.
 */
class GenerateRecoveryCodesUseCase @Inject internal constructor(
    private val recoveryCodeEncoderService: RecoveryCodeEncoderService
) {

    /**
     * Stores the number of recovery codes that are generated.
     */
    private val numberOfRecoveryCodes: Int = 5


    /**
     * Generates new recovery codes. Codes are returned as list of CharArrays that are encoded using
     * Crockford's Base32 (e.g. 'ABCD1234EFGH5678JKMN1234'), which can later be formatted into 6
     * segments with 4 characters each ('ABCD-1234-EFGH-5678-JKMN-1234') for displaying to the user.
     *
     * @return  List of recovery codes.
     */
    suspend fun generate(): List<CharArray> = coroutineScope {
        val recoveryCodes: MutableList<CharArray> = mutableListOf()
        val random = SecureRandom()

        for (i: Int in 0 until numberOfRecoveryCodes) {
            //Recovery codes are encoded with Crockford's Base32: XXXX-XXXX-XXXX-XXXX-XXXX-XXXX
            //6 Segments * 4 Characters * 5 Bits per character = 120 Bits
            val recoveryCode = ByteArray((6 * 4 * 5) / 8) //Divide by 8 to get bytes
            random.nextBytes(recoveryCode)

            val encoded: CharArray = recoveryCodeEncoderService.encode(recoveryCode)
            recoveryCodes.add(encoded)
        }

        return@coroutineScope recoveryCodes
    }

}
