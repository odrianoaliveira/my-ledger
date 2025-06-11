package com.adriano.ledger.account

import com.adriano.ledger.transaction.Direction
import com.adriano.ledger.transaction.TransactionService
import java.util.*

class AccountService(private val transactionService: TransactionService) {

    fun createAccount(name: String, ownerId: UUID): Account {
        val account = Account(
            id = UUID.randomUUID(),
            name = name,
            ownerId = ownerId
        )
        return AccountRepository.save(account)
    }

    fun getAllAccounts() = AccountRepository.findAll()

    fun getAccountById(id: UUID) = AccountRepository.findById(id)

    fun calculateBalance(accountId: UUID): Long {
        return transactionService.getAllTransactions()
            .flatMap { it.entries }
            .filter { it.accountId == accountId }
            .sumOf { entry ->
                when (entry.direction) {
                    Direction.DEBIT -> -1 * entry.amount
                    Direction.CREDIT -> entry.amount
                }
            }
    }
}
