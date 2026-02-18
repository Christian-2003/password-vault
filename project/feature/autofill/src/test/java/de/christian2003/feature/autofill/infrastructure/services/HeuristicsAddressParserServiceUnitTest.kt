package de.christian2003.feature.autofill.infrastructure.services

import de.christian2003.feature.autofill.domain.entities.AutofillType
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class HeuristicsAddressParserServiceUnitTest {

    private lateinit var parser: HeuristicAddressParserService


    @Before
    fun setup() {
        parser = HeuristicAddressParserService()
    }


    @Test
    fun `blank address returns empty map`() = runTest {
        val result = parser.parseAddressToParts("   ")
        Assert.assertTrue(result.isEmpty())
    }


    @Test
    fun `parses German style address`() = runTest {
        val input = "Musterstraße 12, 10115 Berlin"

        val result = parser.parseAddressToParts(input)

        Assert.assertEquals("Musterstraße 12", result[AutofillType.AddressStreet])
        Assert.assertEquals("10115", result[AutofillType.PostalCode])
        Assert.assertEquals("Berlin", result[AutofillType.AddressLocality])
        Assert.assertEquals(input, result[AutofillType.PostalAddress])
    }


    @Test
    fun `parses French style address`() = runTest {
        val input = "10 Rue de la Paix, 75002 Paris"

        val result = parser.parseAddressToParts(input)

        Assert.assertEquals("10 Rue de la Paix", result[AutofillType.AddressStreet])
        Assert.assertEquals("75002", result[AutofillType.PostalCode])
        Assert.assertEquals("Paris", result[AutofillType.AddressLocality])
    }


    @Test
    fun `parses Italian style address`() = runTest {
        val input = "Via Roma 25, 00184 Roma"

        val result = parser.parseAddressToParts(input)

        Assert.assertEquals("Via Roma 25", result[AutofillType.AddressStreet])
        Assert.assertEquals("00184", result[AutofillType.PostalCode])
        Assert.assertEquals("Roma", result[AutofillType.AddressLocality])
    }


    @Test
    fun `parses address with country`() = runTest {
        val input = "Main Street 123, 62704 Springfield, Germany"

        val result = parser.parseAddressToParts(input)

        Assert.assertEquals("Main Street 123", result[AutofillType.AddressStreet])
        Assert.assertEquals("62704", result[AutofillType.PostalCode])
        Assert.assertEquals("Springfield", result[AutofillType.AddressLocality])
        Assert.assertEquals("Germany", result[AutofillType.AddressCountry])
    }


    @Test
    fun `parses street number first`() = runTest {
        val input = "12 Main Street, 10115 Berlin"

        val result = parser.parseAddressToParts(input)

        Assert.assertEquals("12 Main Street", result[AutofillType.AddressStreet])
        Assert.assertEquals("10115", result[AutofillType.PostalCode])
        Assert.assertEquals("Berlin", result[AutofillType.AddressLocality])
    }


    @Test
    fun `handles multi line input`() = runTest {
        val input = """
            Main Street 123
            62704 Springfield
        """.trimIndent()

        val result = parser.parseAddressToParts(input)

        Assert.assertEquals("Main Street 123", result[AutofillType.AddressStreet])
        Assert.assertEquals("62704", result[AutofillType.PostalCode])
        Assert.assertEquals("Springfield", result[AutofillType.AddressLocality])
    }


    @Test
    fun `handles address without postal code`() = runTest {
        val input = "Main Street 123, Springfield"

        val result = parser.parseAddressToParts(input)

        Assert.assertEquals("Main Street 123", result[AutofillType.AddressStreet])
        Assert.assertNull(result[AutofillType.PostalCode])
        Assert.assertNull(result[AutofillType.AddressLocality])
    }


    @Test
    fun `handles only street`() = runTest {
        val input = "Main Street 123"

        val result = parser.parseAddressToParts(input)

        Assert.assertEquals("Main Street 123", result[AutofillType.AddressStreet])
        Assert.assertEquals(input, result[AutofillType.PostalAddress])
    }

}
