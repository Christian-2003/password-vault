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
        Assert.assertEquals(QueryTokenType.Literal, tokens.peek()!!.type)
        Assert.assertEquals("hello", tokens.consume().value)
    }


    @Test
    fun `tokenize multiple literals`() {
        val tokens = tokenizer.tokenize("hello world")

        Assert.assertEquals(2, tokens.size)
        Assert.assertEquals("hello", tokens.consume().value)
        Assert.assertEquals("world", tokens.consume().value)
    }


    @Test
    fun `tokenize quoted literal`() {
        val tokens = tokenizer.tokenize("\"hello world\"")

        Assert.assertEquals(1, tokens.size)
        Assert.assertEquals(QueryTokenType.Literal, tokens.peek()!!.type)
        Assert.assertEquals("hello world", tokens.consume().value)
    }


    @Test
    fun `tokenize parentheses`() {
        val tokens = tokenizer.tokenize("(hello)")

        Assert.assertEquals(3, tokens.size)
        Assert.assertEquals(QueryTokenType.ParenthesesOpen, tokens.peek()!!.type)
        Assert.assertEquals("(", tokens.consume().value)

        Assert.assertEquals(QueryTokenType.Literal, tokens.peek()!!.type)
        Assert.assertEquals("hello", tokens.consume().value)

        Assert.assertEquals(QueryTokenType.ParenthesesClose, tokens.peek()!!.type)
        Assert.assertEquals(")", tokens.consume().value)
    }


    @Test
    fun `tokenize boolean operators`() {
        val tokens = tokenizer.tokenize("AND or")

        Assert. assertEquals(2, tokens.size)

        Assert.assertEquals(QueryTokenType.OperatorBool, tokens.peek()!!.type)
        Assert.assertEquals("and", tokens.consume().value)

        Assert.assertEquals(QueryTokenType.OperatorBool, tokens.peek()!!.type)
        Assert.assertEquals("or", tokens.consume().value)
    }


    @Test
    fun `tokenize relational operators`() {
        val tokens = tokenizer.tokenize("<= > < >= <>")

        Assert.assertEquals(5, tokens.size)

        Assert.assertEquals(QueryTokenType.OperatorRelation, tokens.peek()!!.type)
        Assert.assertEquals("<=", tokens.consume().value)

        Assert.assertEquals(QueryTokenType.OperatorRelation, tokens.peek()!!.type)
        Assert.assertEquals(">", tokens.consume().value)

        Assert.assertEquals(QueryTokenType.OperatorRelation, tokens.peek()!!.type)
        Assert.assertEquals("<", tokens.consume().value)

        Assert.assertEquals(QueryTokenType.OperatorRelation, tokens.peek()!!.type)
        Assert.assertEquals(">=", tokens.consume().value)

        Assert.assertEquals(QueryTokenType.OperatorRelation, tokens.peek()!!.type)
        Assert.assertEquals("<>", tokens.consume().value)
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
            QueryToken(QueryTokenType.Literal, "Bank account"),
            QueryToken(QueryTokenType.ParenthesesClose, ")"),
            QueryToken(QueryTokenType.OperatorBool, "and"),
            QueryToken(QueryTokenType.Literal, "created"),
            QueryToken(QueryTokenType.Colon, ":"),
            QueryToken(QueryTokenType.OperatorRelation, "<="),
            QueryToken(QueryTokenType.Literal, "2025-11-12")
        )

        Assert.assertEquals(expected.size, tokens.size)

        for (i in expected.indices) {
            val actual = tokens.consume()
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
