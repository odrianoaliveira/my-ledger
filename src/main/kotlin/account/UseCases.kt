package com.adriano.account

import java.util.*

private val accounts = mutableListOf<Account>()

fun createAccount(name: String, ownerId: String): Account {
    val account = Account(
        id = UUID.randomUUID().toString(),
        name = name,
        ownerId = ownerId
    )
    accounts.add(account)
    return account
}

fun listAllAccounts(): List<Account> {
    return accounts
}

fun getAccountById(id: String): Account? {
    return accounts.find { it.id == id }
}