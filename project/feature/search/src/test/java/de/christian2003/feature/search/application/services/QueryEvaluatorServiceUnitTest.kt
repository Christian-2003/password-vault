package de.christian2003.feature.search.application.services

import androidx.core.net.toUri
import de.christian2003.data.accounts.application.usecases.GetLocalizedPackageNameUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.AccountMetadata
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.entities.Target
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate


@RunWith(RobolectricTestRunner::class)
class QueryEvaluatorServiceUnitTest {

    private lateinit var tokenizer: QueryTokenizerService
    private lateinit var parser: QueryParserService
    private lateinit var evaluator: QueryEvaluatorService


    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        val localizedUseCase = mock<GetLocalizedPackageNameUseCase>()
        whenever(localizedUseCase.getLocalizedPackageName(any())).thenReturn("Password Vault")

        tokenizer = QueryTokenizerService()
        parser = QueryParserService()
        evaluator = QueryEvaluatorService(localizedUseCase)
    }


    private fun evaluate(query: String, account: Account): Boolean {
        val tokens = tokenizer.tokenize(query)
        val ast = parser.parse(tokens)
        return evaluator.evaluate(ast!!, account)
    }


    private fun buildAccount(
        name: String = "Bank",
        description: String = "Primary bank account",
        tags: List<String> = listOf("finance"),
        target: String = "de.christian2003.passwordvault",
        createdAt: LocalDate = LocalDate.of(2025, 1, 1),
        editedAt: LocalDate = LocalDate.of(2025, 1, 2)
    ): Account {
        return Account(
            descriptor = AccountDescriptor(
                name = name,
                description = description,
                targets = listOf(
                    Target(
                        name = target,
                        url = "android://abcdefghi@${target}".toUri()
                    )
                )
            ),
            tags = tags.map { Tag(it) },
            metadata = AccountMetadata(
                createdAt = createdAt.atStartOfDay(),
                editedAt = editedAt.atStartOfDay()
            )
        )
    }


    @Test
    fun `name equality should match`() {
        val account = buildAccount(name = "Bank")
        Assert.assertTrue(evaluate("name:Bank", account))
    }


    @Test
    fun `name equality should be case insensitive`() {
        val account = buildAccount(name = "Bank")
        Assert.assertTrue(evaluate("name:bank", account))
    }


    @Test
    fun `description equality should match`() {
        val account = buildAccount(description = "Secret account")
        Assert.assertTrue(evaluate("description:secret", account))
    }


    @Test
    fun `tag equality should match`() {
        val account = buildAccount(tags = listOf("work", "finance"))
        Assert.assertTrue(evaluate("tag:finance", account))
    }


    @Test
    fun `createdAt equality should match`() {
        val account = buildAccount(createdAt = LocalDate.of(2025, 1, 1), editedAt = LocalDate.of(2025, 1, 2))
        Assert.assertTrue(evaluate("createdAt:2025-01-01", account))
    }


    @Test
    fun `invalid date should return false`() {
        val account = buildAccount()
        Assert.assertFalse(evaluate("createdAt:invalid", account))
    }


    @Test
    fun `greater than should match`() {
        val account = buildAccount(createdAt = LocalDate.of(2025, 1, 5), editedAt = LocalDate.of(2025, 1, 6))
        Assert.assertTrue(evaluate("createdAt:>2025-01-01", account))
    }


    @Test
    fun `less than should match`() {
        val account = buildAccount(createdAt = LocalDate.of(2025, 1, 1))
        Assert.assertTrue(evaluate("createdAt:<2025-02-01", account))
    }


    @Test
    fun `less than or equal should match`() {
        val account = buildAccount(createdAt = LocalDate.of(2025, 1, 1))
        Assert.assertTrue(evaluate("createdAt:<=2025-01-01", account))
    }


    @Test
    fun `inequality should work for strings`() {
        val account = buildAccount(name = "Bank")
        Assert.assertTrue(evaluate("name:<>Google", account))
    }


    @Test
    fun `inequality should fail when equal`() {
        val account = buildAccount(name = "Bank")
        Assert.assertFalse(evaluate("name:<>Bank", account))
    }


    @Test
    fun `and operator should require both sides true`() {
        val account = buildAccount(
            name = "Bank",
            description = "Primary account"
        )

        Assert.assertTrue(
            evaluate(
                "name:Bank AND description:Primary",
                account
            )
        )
    }


    @Test
    fun `or operator should require one side true`() {
        val account = buildAccount(
            name = "Bank",
            description = "Primary account"
        )

        Assert.assertTrue(
            evaluate(
                "name:Bank OR description:NotMatching",
                account
            )
        )
    }


    @Test
    fun `complex combined query should evaluate correctly`() {
        val account = buildAccount(
            name = "Bank",
            description = "Primary account",
            createdAt = LocalDate.of(2025, 1, 5),
            editedAt = LocalDate.of(2025, 1, 6)
        )

        val query = "(name:Bank OR name:\"Bank account\") AND createdAt:>2024-01-01"

        Assert.assertTrue(evaluate(query, account))
    }


    @Test
    fun `unknown field should return false`() {
        val account = buildAccount()
        Assert.assertFalse(evaluate("unknown:Bank", account))
    }

}
