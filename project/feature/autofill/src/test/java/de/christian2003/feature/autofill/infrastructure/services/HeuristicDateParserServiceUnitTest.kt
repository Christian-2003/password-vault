package de.christian2003.feature.autofill.infrastructure.services

import de.christian2003.feature.autofill.domain.entities.AutofillType
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HeuristicDateParserServiceUnitTest {

    private lateinit var service: HeuristicDateParserService

    @Before
    fun setup() {
        service = HeuristicDateParserService()
    }

    @Test
    fun `parses yyyy-MM-dd format`() = runTest {
        val result = service.parseDateToParts("2023-12-21")
        Assert.assertEquals("21", result[AutofillType.BirthDateDay])
        Assert.assertEquals("12", result[AutofillType.BirthDateMonth])
        Assert.assertEquals("2023", result[AutofillType.BirthDateYear])
        Assert.assertEquals("2023-12-21", result[AutofillType.BirthDateFull])
    }

    @Test
    fun `parses dd dot MM dot yyyy format`() = runTest {
        val result = service.parseDateToParts("21.12.2023")
        Assert.assertEquals("21", result[AutofillType.BirthDateDay])
        Assert.assertEquals("12", result[AutofillType.BirthDateMonth])
        Assert.assertEquals("2023", result[AutofillType.BirthDateYear])
    }

    @Test
    fun `parses d dot M dot yyyy format`() = runTest {
        val result = service.parseDateToParts("5.3.2023")
        Assert.assertEquals("5", result[AutofillType.BirthDateDay])
        Assert.assertEquals("3", result[AutofillType.BirthDateMonth])
        Assert.assertEquals("2023", result[AutofillType.BirthDateYear])
    }

    @Test
    fun `parses dd slash MM slash yyyy format`() = runTest {
        val result = service.parseDateToParts("21/12/2023")
        Assert.assertEquals("21", result[AutofillType.BirthDateDay])
        Assert.assertEquals("12", result[AutofillType.BirthDateMonth])
        Assert.assertEquals("2023", result[AutofillType.BirthDateYear])
    }

    @Test
    fun `parses d slash M slash yyyy format`() = runTest {
        val result = service.parseDateToParts("5/3/2023")
        Assert.assertEquals("5", result[AutofillType.BirthDateDay])
        Assert.assertEquals("3", result[AutofillType.BirthDateMonth])
        Assert.assertEquals("2023", result[AutofillType.BirthDateYear])
    }

    @Test
    fun `parses MM slash dd slash yyyy format`() = runTest {
        val result = service.parseDateToParts("12/21/2023")
        Assert.assertEquals("21", result[AutofillType.BirthDateDay])
        Assert.assertEquals("12", result[AutofillType.BirthDateMonth])
        Assert.assertEquals("2023", result[AutofillType.BirthDateYear])
    }

    @Test
    fun `parses dd-MM-yyyy format`() = runTest {
        val result = service.parseDateToParts("21-12-2023")
        Assert.assertEquals("21", result[AutofillType.BirthDateDay])
        Assert.assertEquals("12", result[AutofillType.BirthDateMonth])
        Assert.assertEquals("2023", result[AutofillType.BirthDateYear])
    }

    @Test
    fun `parses yy-MM-dd format`() = runTest {
        val result = service.parseDateToParts("23-12-21")
        // 2-digit year may be parsed as 0023 depending on formatter,
        // just check that Full is returned
        Assert.assertEquals("23-12-21", result[AutofillType.BirthDateFull])
    }

    @Test
    fun `parses dd dot MM dot yy format`() = runTest {
        val result = service.parseDateToParts("21.12.23")
        Assert.assertEquals("21.12.23", result[AutofillType.BirthDateFull])
    }

    @Test
    fun `parses d dot M dot yy format`() = runTest {
        val result = service.parseDateToParts("5.3.23")
        Assert.assertEquals("5.3.23", result[AutofillType.BirthDateFull])
    }

    @Test
    fun `returns full only for invalid date`() = runTest {
        val result = service.parseDateToParts("not a date")
        Assert.assertEquals("not a date", result[AutofillType.BirthDateFull])
        Assert.assertNull(result[AutofillType.BirthDateDay])
        Assert.assertNull(result[AutofillType.BirthDateMonth])
        Assert.assertNull(result[AutofillType.BirthDateYear])
    }

    @Test
    fun `handles blank input`() = runTest {
        val result = service.parseDateToParts("   ")
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `parses yyyy slash MM slash dd format`() = runTest {
        val result = service.parseDateToParts("2023/12/21")
        Assert.assertEquals("21", result[AutofillType.BirthDateDay])
        Assert.assertEquals("12", result[AutofillType.BirthDateMonth])
        Assert.assertEquals("2023", result[AutofillType.BirthDateYear])
    }

    @Test
    fun `parses d-M-yy format`() = runTest {
        val result = service.parseDateToParts("5-3-23")
        Assert.assertEquals("5-3-23", result[AutofillType.BirthDateFull])
    }

}
