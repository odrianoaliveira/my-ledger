package com.adriano.ledger.transaction

import com.adriano.ledger.common.Repository
import java.util.*

object TransactionRepository : Repository<Transaction> {
    private val transactions = mutableMapOf<UUID, Transaction>()

    override fun save(value: Transaction): Transaction {
        transactions[value.id] = value
        return transactions[value.id]!!
    }

    override fun findById(id: UUID): Transaction? = transactions[id]

    override fun findAll(): List<Transaction> = transactions.values.toList()
}
