package com.adriano.ledger.account

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.accountRoutes() {
    route("/account") {
        post {
            val request = call.receive<CreateAccountRequest>()
            val newAccount = AccountService.createAccount(request.name, request.ownerId)
            call.respond(HttpStatusCode.Created, newAccount)
        }

        get {
            call.respond(HttpStatusCode.OK, AccountService.getAllAccounts())
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

            val account = AccountService.getAccountById(accountId)
            if (account == null) {
                call.respond(HttpStatusCode.NotFound, "Account not found")
                return@get
            }

            call.respond(HttpStatusCode.OK, account)
        }

        get("/{id}/balance") {
            call.respond(HttpStatusCode.NotImplemented, "not implemented")
        }
    }
}