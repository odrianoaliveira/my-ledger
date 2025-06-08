package com.adriano.account

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.accountRoutes() {
    route("/account") {
        post {
            val request = call.receive<CreateAccountRequest>()
            val newAccount = Account(
                id = UUID.randomUUID().toString(),
                name = request.name,
                ownerId = request.ownerId
            )
            call.respond(HttpStatusCode.Created, newAccount)
        }
    }
}