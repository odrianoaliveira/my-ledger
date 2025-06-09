package com.adriano.ledger.transaction

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.transactionRoutes() {
    route("/transaction") {

        post {
            val request = call.receive<CreateTransactionRequest>()
            val transaction = createTransaction(request)
            call.respond(HttpStatusCode.Created, transaction)
        }

        get {
            val transactions = getAllTransactions()
            call.respond(HttpStatusCode.OK, transactions)
        }

        get("/{id}") {
            val idParam = call.parameters["id"]
            val id = try {
                UUID.fromString(idParam)
            } catch (_: Exception) {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid UUID format")
            }

            val transaction = getTransactionById(id)
            if (transaction == null) {
                call.respond(HttpStatusCode.NotFound, "Transaction not found")
            } else {
                call.respond(transaction)
            }
        }
    }
}
