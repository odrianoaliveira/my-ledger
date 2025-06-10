package com.adriano.ledger.account

import com.adriano.ledger.common.Repository
import java.util.*

object AccountRepository : Repository<Account> {
    private val accounts = mutableMapOf<UUID, Account>()

    override fun save(value: Account): Account {
        accounts[value.id] = value
        return accounts[value.id]!!
    }

    override fun findById(id: UUID) = accounts[id]

    override fun findAll() = accounts.values.toList()

}