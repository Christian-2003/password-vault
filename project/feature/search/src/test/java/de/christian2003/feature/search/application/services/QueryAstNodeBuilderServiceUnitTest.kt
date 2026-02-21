package de.christian2003.feature.search.application.services

import de.christian2003.feature.search.domain.entities.QueryAstNode
import de.christian2003.feature.search.domain.entities.QueryTokenType
import org.junit.Assert
import org.junit.Test


class QueryAstNodeBuilderServiceUnitTest {

    private val builder = QueryAstNodeBuilderService()


    private fun build(query: String): QueryAstNode? {
        val tokenizer = QueryTokenizerService()
        val tokens = tokenizer.tokenize(query)
        return builder.parse(tokens)
    }


    @Test
    fun `parse simple literal`() {
        val ast = build("hello")

        Assert.assertNotNull(ast)
        Assert.assertEquals(QueryTokenType.Literal, ast!!.token.type)
        Assert.assertEquals("hello", ast.token.value)
        Assert.assertNull(ast.left)
        Assert.assertNull(ast.right)
    }


    @Test
    fun `parse quoted literal`() {
        val ast = build("\"hello world\"")

        Assert.assertNotNull(ast)
        Assert.assertEquals(QueryTokenType.Literal, ast!!.token.type)
        Assert.assertEquals("hello world", ast.token.value)
    }


    @Test
    fun `parse comparison`() {
        val ast = build("created:<=2025-01-01")

        Assert.assertNotNull(ast)
        Assert.assertEquals(QueryTokenType.OperatorRelation, ast!!.token.type)
        Assert.assertEquals("<=", ast.token.value)

        Assert.assertNotNull(ast.left)
        Assert.assertEquals(QueryTokenType.Literal, ast.left!!.token.type)
        Assert.assertEquals("created", ast.left!!.token.value)

        Assert.assertNotNull(ast.right)
        Assert.assertEquals(QueryTokenType.Literal, ast.right!!.token.type)
        Assert.assertEquals("2025-01-01", ast.right!!.token.value)
    }


    @Test
    fun `implicit AND between expressions`() {
        val ast = build("name:Bank created:2025")

        Assert.assertNotNull(ast)
        Assert.assertEquals(QueryTokenType.OperatorBool, ast!!.token.type)
        Assert.assertEquals("and", ast.token.value)

        Assert.assertNotNull(ast.left)
        Assert.assertNotNull(ast.right)
    }


    @Test
    fun `explicit AND`() {
        val ast = build("name:Bank AND created:2025")

        Assert.assertNotNull(ast)
        Assert.assertEquals(QueryTokenType.OperatorBool, ast!!.token.type)
        Assert.assertEquals("and", ast.token.value)
    }


    @Test
    fun `OR precedence`() {
        val ast = build("a OR b AND c")

        Assert.assertNotNull(ast)

        // Root should be OR
        Assert.assertEquals(QueryTokenType.OperatorBool, ast!!.token.type)
        Assert.assertEquals("or", ast.token.value)

        // Right side of OR should be AND
        val right = ast.right
        Assert.assertNotNull(right)
        Assert.assertEquals(QueryTokenType.OperatorBool, right!!.token.type)
        Assert.assertEquals("and", right.token.value)
    }


    @Test
    fun `parentheses grouping`() {
        val ast = build("(a OR b) AND c")

        Assert.assertNotNull(ast)
        Assert.assertEquals(QueryTokenType.OperatorBool, ast!!.token.type)
        Assert.assertEquals("and", ast.token.value)

        val left = ast.left
        Assert.assertNotNull(left)
        Assert.assertEquals(QueryTokenType.OperatorBool, left!!.token.type)
        Assert.assertEquals("or", left.token.value)
    }


    @Test
    fun `empty group throws error`() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            build("()")
        }
    }


    @Test
    fun `trailing token throws error`() {
        Assert.assertThrows(IllegalArgumentException::class.java) {
            build("name:Bank )")
        }
    }


    @Test
    fun `complex combined query`() {
        val ast = build("(name:Bank OR name:\"Bank account\") AND created:<=2025-11-12")

        Assert.assertNotNull(ast)
        Assert.assertEquals(QueryTokenType.OperatorBool, ast!!.token.type)
        Assert.assertEquals("and", ast.token.value)
    }


    @Test
    fun `unknown syntax is always root`() {
        val ast = build("@@@")
        Assert.assertNotNull(ast)
        Assert.assertEquals(ast!!.token.value, "@@@")
    }

}
