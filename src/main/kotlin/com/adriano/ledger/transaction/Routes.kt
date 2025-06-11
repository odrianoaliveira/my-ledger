package com.adriano.ledger.transaction

import com.adriano.ledger.account.AccountService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import java.util.*

fun CreateTransactionRequest.validate(service: AccountService) {
    require(description.isNotBlank()) { "Description must not be blank" }
    require(entries.size > 1) { "At least two entries are required" }
    require(timestamp <= Clock.System.now()) { "Timestamp cannot be in the future" }

    entries.forEachIndexed { index, entry ->
        entry.validate(index, service)
    }

    val total = entries.sumOf { entry ->
        when (entry.direction) {
            Direction.DEBIT -> entry.amount
            Direction.CREDIT -> -entry.amount
        }
    }

    require(total == 0L) { "Entries must balance: sum of debits and credits must be zero, but was $total" }
}

fun Entry.validate(index: Int, service: AccountService) {
    require(amount > 0) { "Entry[$index]: amount must be greater than 0" }
    require(service.getAccountById(accountId) != null) { "Entry[$index]: accountId '$accountId' does not exist" }
}

fun Route.transactionRoutes(accountService: AccountService, transactionService: TransactionService) {
    route("/transaction") {

        post {
            val request = try {
                call.receive<CreateTransactionRequest>()
            } catch (e: Exception) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }

            try {
                request.validate(accountService)
            } catch (e: IllegalArgumentException) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
            val transaction = transactionService.createTransaction(request)
            call.respond(HttpStatusCode.Created, transaction)
        }

        get {
            val transactions = transactionService.getAllTransactions()
            call.respond(HttpStatusCode.OK, transactions)
        }

        get("/{id}") {
            val idParam = call.parameters["id"]
            val id = try {
                UUID.fromString(idParam)
            } catch (_: Exception) {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid UUID format")
            }

            val transaction = transactionService.getTransactionById(id)
            if (transaction == null) {
                call.respond(HttpStatusCode.NotFound, "Transaction not found")
            } else {
                call.respond(transaction)
            }
        }
    }

}
