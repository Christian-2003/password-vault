package de.christian2003.security.application.usecases

import de.christian2003.security.application.services.RecoveryCodeEncoderService
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


class GenerateRecoveryCodesUseCaseUnitTest {

    private lateinit var recoveryCodeEncoderService: RecoveryCodeEncoderService
    private lateinit var useCase: GenerateRecoveryCodesUseCase


    @Before
    fun setup() {
        recoveryCodeEncoderService = mock()
        useCase = GenerateRecoveryCodesUseCase(recoveryCodeEncoderService)
    }


    @Test
    fun `generate returns 5 recovery codes`() = runBlocking {
        // Mock encoder to return a dummy char array of the same length
        whenever(recoveryCodeEncoderService.encode(any())).thenAnswer { invocation ->
            val input = invocation.arguments[0] as ByteArray
            CharArray(input.size) { 'A' } // dummy encoding
        }

        val result = useCase.generate()

        // There should be exactly 5 recovery codes
        Assert.assertEquals(5, result.size)

        // Each recovery code should not be empty
        result.forEach { code ->
            Assert.assertTrue(code.isNotEmpty())
        }
    }


    @Test
    fun `generate returns different codes for different runs`() = runBlocking {
        // Simple mock to encode bytes to char array with hash-based pseudo-differentiation
        whenever(recoveryCodeEncoderService.encode(any())).thenAnswer { invocation ->
            val input = invocation.arguments[0] as ByteArray
            input.map { ((it.toInt() and 0xFF) % 26 + 'A'.code).toChar() }.toCharArray()
        }

        val result1 = useCase.generate()
        val result2 = useCase.generate()

        // Codes should be different between runs
        Assert.assertNotEquals(result1.joinToString(), result2.joinToString())
    }

}
