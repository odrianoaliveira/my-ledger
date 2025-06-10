package com.adriano.ledger.transaction

import com.adriano.ledger.account.Account
import com.adriano.ledger.account.AccountService
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CreateTransactionRequestTest {

    @Test
    fun `should pass validation when request is valid`() {
        val accountId = UUID.randomUUID()
        val request = CreateTransactionRequest(
            description = "Valid transaction",
            timestamp = Clock.System.now(),
            entries = listOf(
                Entry(accountId, 1000L, Direction.DEBIT),
                Entry(accountId, 1000L, Direction.CREDIT),
            )
        )

        val service = mockk<AccountService>()
        every { service.getAccountById(accountId) } returns mockk<Account>()

        assertDoesNotThrow {
            request.validate(service)
        }
    }

    @Test
    fun `should fail when description is blank`() {
        val accountId = UUID.randomUUID()
        val request = CreateTransactionRequest(
            description = "   ",
            timestamp = Clock.System.now(),
            entries = listOf(
                Entry(accountId, 1000L, Direction.DEBIT),
                Entry(accountId, 1000L, Direction.CREDIT),
            )
        )

        val service = mockk<AccountService>()
        every { service.getAccountById(accountId) } returns mockk<Account>()

        val exception = assertFailsWith(
            exceptionClass = IllegalArgumentException::class,
        ) {
            request.validate(service)
        }

        assertEquals("Description must not be blank", exception.message)

    }

    @Test
    fun `should fail when entry size is less than 2`() {
        val accountId = UUID.randomUUID()
        val request = CreateTransactionRequest(
            description = "A transaction with missing entry",
            timestamp = Clock.System.now(),
            entries = listOf(
                Entry(accountId, 1000L, Direction.DEBIT)
            )
        )

        val service = mockk<AccountService>()
        every { service.getAccountById(accountId) } returns mockk<Account>()

        val exception = assertFailsWith(
            exceptionClass = IllegalArgumentException::class
        ) {
            request.validate(service)
        }

        assertEquals("At least two entries are required", exception.message)
    }

    @Test
    fun `should fail when timestamp is future`() {
        val accountId = UUID.randomUUID()
        val request = CreateTransactionRequest(
            description = "A transaction from the future",
            timestamp = Instant.DISTANT_FUTURE,
            entries = listOf(
                Entry(accountId, 1000L, Direction.DEBIT),
                Entry(accountId, 1000L, Direction.CREDIT),
            )
        )

        val service = mockk<AccountService>()
        every { service.getAccountById(accountId) } returns mockk<Account>()

        val exception = assertFailsWith(
            exceptionClass = IllegalArgumentException::class
        ) {
            request.validate(service)
        }

        assertEquals("Timestamp cannot be in the future", exception.message)
    }

    @Test
    fun `should fail when account does not exist`() {
        val accountId = UUID.randomUUID()
        val request = CreateTransactionRequest(
            description = "A transaction from the future",
            timestamp = Clock.System.now(),
            entries = listOf(
                Entry(accountId, 1000L, Direction.DEBIT),
                Entry(accountId, 1000L, Direction.CREDIT),
            )
        )

        val service = mockk<AccountService>()
        every { service.getAccountById(accountId) } returns null

        val exception = assertFailsWith(
            exceptionClass = IllegalArgumentException::class
        ) {
            request.validate(service)
        }

        assertEquals("Entry[0]: accountId '$accountId' does not exist", exception.message)
    }

    @Test
    fun `should fail when entries are not balanced`() {
        val accountId = UUID.randomUUID()
        val request = CreateTransactionRequest(
            description = "A transaction from the future",
            timestamp = Clock.System.now(),
            entries = listOf(
                Entry(accountId, 500L, Direction.DEBIT),
                Entry(accountId, 1000L, Direction.CREDIT),
            )
        )

        val service = mockk<AccountService>()
        every { service.getAccountById(accountId) } returns mockk<Account>()

        val exception = assertFailsWith(
            exceptionClass = IllegalArgumentException::class
        ) {
            request.validate(service)
        }

        assertTrue { exception.message!!.contains("Entries must balance") }
    }

}