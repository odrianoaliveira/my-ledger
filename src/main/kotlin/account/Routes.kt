package com.adriano.account

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.accountRoutes() {
    route("/account") {
        post {
            val request = call.receive<CreateAccountRequest>()
            val newAccount = createAccount(request.name, request.ownerId)
            call.respond(HttpStatusCode.Created, newAccount)
        }

        get {
            call.respond(HttpStatusCode.OK, listAllAccounts())
        }
    }
}