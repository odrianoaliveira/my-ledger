package com.adriano.ledger.account

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun CreateAccountRequest.validate() {
    require(name.isNotBlank()) { "Account name cannot be blank" }
}

fun Route.accountRoutes(accountService: AccountService) {
    route("/account") {
        post {
            val request = call.receive<CreateAccountRequest>()
            try {
                request.validate()
            } catch (e: IllegalArgumentException) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
            val newAccount = accountService.createAccount(request.name, request.ownerId)
            call.respond(HttpStatusCode.Created, newAccount)
        }

        get {
            call.respond(HttpStatusCode.OK, accountService.getAllAccounts())
        }

        get("/{id}") {
            val idParam = call.parameters["id"]
            if (idParam == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing id")
                return@get
            }

            val accountId = try {
                UUID.fromString(idParam)
            } catch (_: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid UUID format")
                return@get
            }

            val account = accountService.getAccountById(accountId)
            if (account == null) {
                call.respond(HttpStatusCode.NotFound, "Account not found")
                return@get
            }

            call.respond(HttpStatusCode.OK, account)
        }

        get("/{id}/balance") {
            val idParam = call.parameters["id"]
            if (idParam == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing id")
                return@get
            }

            val accountId = try {
                UUID.fromString(idParam)
            } catch (_: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid UUID format")
                return@get
            }

            val account = accountService.getAccountById(accountId)
            if (account == null) {
                return@get call.respond(HttpStatusCode.NotFound, "Account does not exist")
            }

            val datetimeAsStr = call.queryParameters["datetime"]
            if (datetimeAsStr != null && datetimeAsStr.isNotBlank()) {
                try {
                    val datetime = kotlinx.datetime.Instant.parse(datetimeAsStr)
                    val balance = accountService.calculateBalance(account.id, datetime)
                    val balanceResponse = AccountBalanceResponse(
                        account = account,
                        balanceInCents = balance
                    )
                    return@get call.respond(HttpStatusCode.OK, balanceResponse)
                } catch (ex: IllegalArgumentException) {
                    return@get call.respond(HttpStatusCode.BadRequest, "Invalid datetime provided")
                }
            } else {
                val balance = accountService.calculateBalance(account.id)
                val balanceResponse = AccountBalanceResponse(
                    account = account,
                    balanceInCents = balance
                )
                call.respond(HttpStatusCode.OK, balanceResponse)
            }

        }
    }
}