package com.adriano.ledger.transaction

import kotlinx.datetime.Clock
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ServiceTest {

    @Test
    fun `should create an account`() {
        val mariaAccId = UUID.randomUUID()
        val joaoAccId = UUID.randomUUID()
        val amount = 10000L
        val now = Clock.System.now()
        val request = CreateTransactionRequest(
            description = "Maria sent 100 EUR to João",
            timestamp = now,
            entries = listOf(
                Entry(
                    accountId = mariaAccId,
                    amount = amount,
                    direction = Direction.DEBIT
                ),
                Entry(
                    accountId = joaoAccId,
                    amount = amount,
                    direction = Direction.CREDIT
                )
            )
        )

        // when
        val result = TransactionService.createTransaction(request)
        assertEquals(request.description, result.description)
        assertEquals(request.timestamp, result.timestamp)
        assertEquals(request.entries, result.entries)
        assertNotNull(result.id)
        assertTrue { result.createdAt >= now }

        val retrieved = TransactionRepository.findById(result.id)
        assertNotNull(retrieved)
        assertEquals(result.id, retrieved.id)
    }
}