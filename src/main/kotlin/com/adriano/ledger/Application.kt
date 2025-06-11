package com.adriano.ledger

import com.adriano.ledger.account.AccountService
import com.adriano.ledger.account.accountRoutes
import com.adriano.ledger.transaction.TransactionService
import com.adriano.ledger.transaction.transactionRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    val transactionService = TransactionService()
    val accountService = AccountService(transactionService)
    routing {
        accountRoutes(accountService)
        transactionRoutes(accountService, transactionService)
    }
}
