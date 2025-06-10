package com.adriano.ledger.account

import com.adriano.ledger.transaction.Direction
import com.adriano.ledger.transaction.Entry
import com.adriano.ledger.transaction.Transaction
import com.adriano.ledger.transaction.TransactionService
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class AccountServiceTest {
    private lateinit var transactionService: TransactionService
    private lateinit var accountService: AccountService

    @BeforeEach
    fun setUp() {
        transactionService = spyk<TransactionService>()
        accountService = AccountService(transactionService)
    }

    @Test
    fun `balance should be zero when there are no transactions`() {
        val accountId = UUID.randomUUID()
        val transactionService = mockk<TransactionService>()

        every { transactionService.getAllTransactions() } returns emptyList()

        val balance = accountService.calculateBalance(accountId)

        assertEquals(0L, balance)
    }

    @Test
    fun `balance should be zero when account has no matching entries`() {
        val accountId = UUID.randomUUID()
        val otherAccount = UUID.randomUUID()
        every { transactionService.getAllTransactions() } returns listOf(
            Transaction(
                UUID.randomUUID(), "Test",
                listOf(
                    Entry(otherAccount, 1000L, Direction.CREDIT),
                    Entry(otherAccount, 500L, Direction.DEBIT)
                ),
                Clock.System.now(),
                Clock.System.now()
            )
        )

        val balance = accountService.calculateBalance(accountId)

        assertEquals(0L, balance)
    }

    @Test
    fun `should compute balance with only CREDIT entries`() {
        val accountId = UUID.randomUUID()
        every { transactionService.getAllTransactions() } returns listOf(
            Transaction(
                UUID.randomUUID(),
                "Credit Only",
                listOf(
                    Entry(accountId, 100L, Direction.CREDIT),
                    Entry(accountId, 200L, Direction.CREDIT),
                    Entry(accountId, 100L, Direction.CREDIT)
                ),
                Clock.System.now(),
                Clock.System.now()
            )
        )

        val balance = accountService.calculateBalance(accountId)

        assertEquals(400L, balance)
    }

    @Test
    fun `should compute balance with only DEBIT entries`() {
        val accountId = UUID.randomUUID()
        every { transactionService.getAllTransactions() } returns listOf(
            Transaction(
                UUID.randomUUID(),
                "Credit Only",
                listOf(
                    Entry(accountId, 100L, Direction.DEBIT),
                    Entry(accountId, 100L, Direction.DEBIT),
                    Entry(accountId, 100L, Direction.DEBIT)
                ),
                Clock.System.now(),
                Clock.System.now()
            )
        )

        val balance = accountService.calculateBalance(accountId)

        assertEquals(-300L, balance)
    }

    @Test
    fun `should compute balance from multiple transactions`() {
        val accountId = UUID.randomUUID()
        val anotherAccountId = UUID.randomUUID()
        every { transactionService.getAllTransactions() } returns listOf(
            Transaction(
                UUID.randomUUID(), "T1", listOf(
                    Entry(accountId, 100L, Direction.CREDIT),
                    Entry(accountId, 30L, Direction.DEBIT)
                ), Clock.System.now(), Clock.System.now()
            ),
            Transaction(
                UUID.randomUUID(), "T2", listOf(
                    Entry(accountId, 70L, Direction.CREDIT),
                    Entry(accountId, 10L, Direction.DEBIT)
                ), Clock.System.now(), Clock.System.now()
            ),
            Transaction(
                UUID.randomUUID(), "T3", listOf(
                    Entry(anotherAccountId, 20L, Direction.CREDIT),
                    Entry(anotherAccountId, 10L, Direction.DEBIT)
                ), Clock.System.now(), Clock.System.now()
            )
        )

        val balance = accountService.calculateBalance(accountId)

        assertEquals(130L, balance) // (100+70)-(30+10)
    }
}
