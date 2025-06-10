package com.adriano.ledger.account

import java.util.*

object AccountService {

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
}
