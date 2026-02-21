package de.christian2003.feature.search.application.services

import de.christian2003.feature.search.domain.entities.QueryToken
import de.christian2003.feature.search.domain.entities.QueryTokenType
import org.junit.Assert
import org.junit.Test


class QueryTokenizerServiceUnitTest {

    private val tokenizer = QueryTokenizerService()


    @Test
    fun `tokenize simple literal`() {
        val tokens = tokenizer.tokenize("hello")

        Assert.assertEquals(1, tokens.size)
        Assert.assertEquals(QueryTokenType.Literal, tokens[0].type)
        Assert.assertEquals("hello", tokens[0].value)
    }


    @Test
    fun `tokenize multiple literals`() {
        val tokens = tokenizer.tokenize("hello world")

        Assert.assertEquals(2, tokens.size)
        Assert.assertEquals("hello", tokens[0].value)
        Assert.assertEquals("world", tokens[1].value)
    }


    @Test
    fun `tokenize quoted literal`() {
        val tokens = tokenizer.tokenize("\"hello world\"")

        Assert.assertEquals(1, tokens.size)
        Assert.assertEquals(QueryTokenType.QuotedLiteral, tokens[0].type)
        Assert.assertEquals("hello world", tokens[0].value)
    }


    @Test
    fun `tokenize parentheses`() {
        val tokens = tokenizer.tokenize("(hello)")

        Assert.assertEquals(3, tokens.size)
        Assert.assertEquals(QueryTokenType.ParenthesesOpen, tokens[0].type)
        Assert.assertEquals("(", tokens[0].value)

        Assert.assertEquals(QueryTokenType.Literal, tokens[1].type)
        Assert.assertEquals("hello", tokens[1].value)

        Assert.assertEquals(QueryTokenType.ParenthesesClose, tokens[2].type)
        Assert.assertEquals(")", tokens[2].value)
    }


    @Test
    fun `tokenize boolean operators`() {
        val tokens = tokenizer.tokenize("AND or")

        Assert. assertEquals(2, tokens.size)

        Assert.assertEquals(QueryTokenType.OperatorBool, tokens[0].type)
        Assert.assertEquals("and", tokens[0].value)

        Assert.assertEquals(QueryTokenType.OperatorBool, tokens[1].type)
        Assert.assertEquals("or", tokens[1].value)
    }


    @Test
    fun `tokenize relational operators`() {
        val tokens = tokenizer.tokenize("<= > < >= <>")

        Assert.assertEquals(5, tokens.size)

        Assert.assertEquals(QueryTokenType.OperatorRelation, tokens[0].type)
        Assert.assertEquals("<=", tokens[0].value)

        Assert.assertEquals(QueryTokenType.OperatorRelation, tokens[1].type)
        Assert.assertEquals(">", tokens[1].value)

        Assert.assertEquals(QueryTokenType.OperatorRelation, tokens[2].type)
        Assert.assertEquals("<", tokens[2].value)

        Assert.assertEquals(QueryTokenType.OperatorRelation, tokens[3].type)
        Assert.assertEquals(">=", tokens[3].value)

        Assert.assertEquals(QueryTokenType.OperatorRelation, tokens[4].type)
        Assert.assertEquals("<>", tokens[4].value)
    }


    @Test
    fun `tokenize combined expression`() {
        val tokens = tokenizer.tokenize("(name:Bank OR name:\"Bank account\") AND created:<=2025-11-12")

        Assert.assertTrue(tokens.isNotEmpty())

        val expected = listOf(
            QueryToken(QueryTokenType.ParenthesesOpen, "("),
            QueryToken(QueryTokenType.Literal, "name"),
            QueryToken(QueryTokenType.Colon, ":"),
            QueryToken(QueryTokenType.Literal, "Bank"),
            QueryToken(QueryTokenType.OperatorBool, "or"),
            QueryToken(QueryTokenType.Literal, "name"),
            QueryToken(QueryTokenType.Colon, ":"),
            QueryToken(QueryTokenType.QuotedLiteral, "Bank account"),
            QueryToken(QueryTokenType.ParenthesesClose, ")"),
            QueryToken(QueryTokenType.OperatorBool, "and"),
            QueryToken(QueryTokenType.Literal, "created"),
            QueryToken(QueryTokenType.Colon, ":"),
            QueryToken(QueryTokenType.OperatorRelation, "<="),
            QueryToken(QueryTokenType.Literal, "2025-11-12")
        )

        Assert.assertEquals(expected.size, tokens.size)

        for (i in expected.indices) {
            val actual = tokens[i]
            val exp = expected[i]

            Assert.assertEquals(exp.type, actual.type)
            Assert.assertEquals(exp.value, actual.value)
        }
    }


    @Test
    fun `unterminated quote throws exception`() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            tokenizer.tokenize("\"hello")
        }
    }

}
