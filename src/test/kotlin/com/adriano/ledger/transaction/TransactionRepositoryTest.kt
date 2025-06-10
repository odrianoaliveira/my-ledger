package com.adriano.ledger.transaction

import kotlinx.datetime.Clock
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TransactionRepositoryTest {

    private fun createTransaction(id: UUID = UUID.randomUUID()): Transaction {
        return Transaction(
            id = id,
            description = "Test transaction",
            timestamp = Clock.System.now(),
            createdAt = Clock.System.now(),
            entries = emptyList()
        )
    }

    @Test
    fun `should save a transaction`() {
        // given
        val transaction = createTransaction()

        // when
        val result = TransactionRepository.save(transaction)

        //then
        assertEquals(transaction, result)
    }

    @Test
    fun `should find the transaction when id is given`() {
        // given
        val transactionId = UUID.randomUUID()
        val transaction = createTransaction(transactionId)
        TransactionRepository.save(transaction)

        // when
        val result = TransactionRepository.findById(transactionId)

        //then
        assertEquals(transaction, result)
    }

    @Test
    fun `should return null when id does not exist`() {
        val result = TransactionRepository.findById(UUID.randomUUID())
        assertNull(result)
    }

    @Test
    fun `should return all transactions`() {
        // given
        val transaction1 = createTransaction()
        TransactionRepository.save(transaction1)
        val transaction2 = createTransaction()
        TransactionRepository.save(transaction2)

        // when
        val result = TransactionRepository.findAll()

        // then
        assertTrue { result.containsAll(listOf(transaction1, transaction2)) }
    }
}