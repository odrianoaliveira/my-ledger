package com.adriano.ledger.transaction

import kotlinx.datetime.Clock
import java.util.*

object TransactionService {
    fun createTransaction(createTransaction: CreateTransactionRequest): Transaction {
        val transaction = Transaction(
            id = UUID.randomUUID(),
            description = createTransaction.description,
            timestamp = createTransaction.timestamp,
            createdAt = Clock.System.now(),
            entries = createTransaction.entries
        )
        return TransactionRepository.save(transaction)
    }

    fun getTransactionById(id: UUID) = TransactionRepository.findById(id)

    fun getAllTransactions(): List<Transaction> = TransactionRepository.findAll()
}
