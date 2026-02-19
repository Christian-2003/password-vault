package de.christian2003.feature.autofill.infrastructure.services

import de.christian2003.feature.autofill.domain.entities.AutofillType
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class LibPhoneNumberParserServiceUnitTest {

    private lateinit var service: LibPhoneNumberParserService


    @Before
    fun setup() {
        service = LibPhoneNumberParserService()
    }


    @Test
    fun `parses german international number`() = runTest {
        val result = service.parsePhoneNumberToParts("+49 170 1234567")

        Assert.assertEquals("+49", result[AutofillType.PhoneCountryCode])
        Assert.assertEquals("+491701234567", result[AutofillType.PhoneNumber])
        Assert.assertEquals("0170 1234567", result[AutofillType.PhoneNumberNational])
        Assert.assertEquals("+49 170 1234567", result[AutofillType.PhoneNumberDevice])
    }


    @Test
    fun `parses spanish international number`() = runTest {
        val result = service.parsePhoneNumberToParts("+34 612 34 56 78")

        Assert.assertEquals("+34", result[AutofillType.PhoneCountryCode])
        Assert.assertEquals("+34612345678", result[AutofillType.PhoneNumber])
    }


    @Test
    fun `returns empty map for invalid number`() = runTest {
        val result = service.parsePhoneNumberToParts("123")

        Assert.assertTrue(result.isEmpty())
    }


    @Test
    fun `returns empty map for blank input`() = runTest {
        val result = service.parsePhoneNumberToParts("   ")

        Assert.assertTrue(result.isEmpty())
    }


    @Test
    fun `parses number with extension`() = runTest {
        val result = service.parsePhoneNumberToParts("+1 650-253-0000")

        Assert.assertEquals("+1", result[AutofillType.PhoneCountryCode])
        Assert.assertEquals("+16502530000", result[AutofillType.PhoneNumber])
    }


    @Test
    fun `device format is international format`() = runTest {
        val result = service.parsePhoneNumberToParts("+49 170 1234567")

        val deviceFormat = result[AutofillType.PhoneNumberDevice]
        Assert.assertTrue(deviceFormat!!.startsWith("+49"))
    }

}
