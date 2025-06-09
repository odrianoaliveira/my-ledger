package com.adriano.ledger.transaction

import java.util.*

object TransactionRepository {
    private val transactions = mutableMapOf<UUID, Transaction>()

    fun save(transaction: Transaction): Transaction {
        transactions[transaction.id] = transaction
        return transactions[transaction.id]!!
    }

    fun findById(id: UUID): Transaction? = transactions[id]

    fun findAll(): List<Transaction> = transactions.values.toList()
}
