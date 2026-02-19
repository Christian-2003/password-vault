package de.christian2003.feature.autofill.infrastructure.services

import de.christian2003.feature.autofill.domain.entities.AutofillType
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class HeuristicPersonNameParserServiceUnitTest {

    private lateinit var service: HeuristicPersonNameParserService


    @Before
    fun setup() {
        service = HeuristicPersonNameParserService()
    }


    @Test
    fun `parses simple first and last name`() = runTest {
        val result = service.parseNameToParts("John Smith")

        Assert.assertEquals("John", result[AutofillType.PersonFirstName])
        Assert.assertEquals("Smith", result[AutofillType.PersonLastName])
        Assert.assertEquals("John Smith", result[AutofillType.PersonFullName])
    }


    @Test
    fun `parses name with middle name`() = runTest {
        val result = service.parseNameToParts("John Michael Smith")

        Assert.assertEquals("John", result[AutofillType.PersonFirstName])
        Assert.assertEquals("Michael", result[AutofillType.PersonMiddleName])
        Assert.assertEquals("Smith", result[AutofillType.PersonLastName])
    }


    @Test
    fun `parses name with middle initial`() = runTest {
        val result = service.parseNameToParts("John M. Smith")

        Assert.assertEquals("John", result[AutofillType.PersonFirstName])
        Assert.assertEquals("M.", result[AutofillType.PersonMiddleInitial])
        Assert.assertEquals("Smith", result[AutofillType.PersonLastName])
    }


    @Test
    fun `parses name with prefix`() = runTest {
        val result = service.parseNameToParts("Dr. John Smith")

        Assert.assertEquals("Dr.", result[AutofillType.PersonNamePrefix])
        Assert.assertEquals("John", result[AutofillType.PersonFirstName])
        Assert.assertEquals("Smith", result[AutofillType.PersonLastName])
    }


    @Test
    fun `parses name with suffix`() = runTest {
        val result = service.parseNameToParts("John Smith Jr.")

        Assert.assertEquals("Jr.", result[AutofillType.PersonNameSuffix])
        Assert.assertEquals("John", result[AutofillType.PersonFirstName])
        Assert.assertEquals("Smith", result[AutofillType.PersonLastName])
    }


    @Test
    fun `parses german surname particle`() = runTest {
        val result = service.parseNameToParts("Ludwig van Beethoven")

        Assert.assertEquals("Ludwig", result[AutofillType.PersonFirstName])
        Assert.assertEquals("van Beethoven", result[AutofillType.PersonLastName])
    }


    @Test
    fun `parses french multi word particle`() = runTest {
        val result = service.parseNameToParts("Jean de la Fontaine")

        Assert.assertEquals("Jean", result[AutofillType.PersonFirstName])
        Assert.assertEquals("de la Fontaine", result[AutofillType.PersonLastName])
    }


    @Test
    fun `parses spanish particle`() = runTest {
        val result = service.parseNameToParts("Juan Carlos de los Santos")

        Assert.assertEquals("Juan", result[AutofillType.PersonFirstName])
        Assert.assertEquals("Carlos", result[AutofillType.PersonMiddleName])
        Assert.assertEquals("de los Santos", result[AutofillType.PersonLastName])
    }


    @Test
    fun `parses mononym`() = runTest {
        val result = service.parseNameToParts("Madonna")

        Assert.assertEquals("Madonna", result[AutofillType.PersonFirstName])
        Assert.assertNull(result[AutofillType.PersonLastName])
    }


    @Test
    fun `handles excessive whitespace`() = runTest {
        val result = service.parseNameToParts("  John   Michael   Smith  ")

        Assert.assertEquals("John", result[AutofillType.PersonFirstName])
        Assert.assertEquals("Michael", result[AutofillType.PersonMiddleName])
        Assert.assertEquals("Smith", result[AutofillType.PersonLastName])
    }


    @Test
    fun `parses complex german noble name`() = runTest {
        val result = service.parseNameToParts("Karl Theodor zu Guttenberg")

        Assert.assertEquals("Karl", result[AutofillType.PersonFirstName])
        Assert.assertEquals("Theodor", result[AutofillType.PersonMiddleName])
        Assert.assertEquals("zu Guttenberg", result[AutofillType.PersonLastName])
    }


    @Test
    fun `parses french apostrophe name without particle split`() = runTest {
        val result = service.parseNameToParts("Jean d'Arc")

        // Current implementation does NOT split d'
        Assert.assertEquals("Jean", result[AutofillType.PersonFirstName])
        Assert.assertEquals("d'Arc", result[AutofillType.PersonLastName])
    }


    @Test
    fun `parses spanish double surname as middle and last`() = runTest {
        val result = service.parseNameToParts("Gabriel García Márquez")

        // Expected behavior of current heuristic
        Assert.assertEquals("Gabriel", result[AutofillType.PersonFirstName])
        Assert.assertEquals("García", result[AutofillType.PersonMiddleName])
        Assert.assertEquals("Márquez", result[AutofillType.PersonLastName])
    }


    @Test
    fun `returns empty map for blank input`() = runTest {
        val result = service.parseNameToParts("   ")

        Assert.assertTrue(result.isEmpty())
    }

}
