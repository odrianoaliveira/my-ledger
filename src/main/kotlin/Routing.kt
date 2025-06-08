package com.adriano

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

fun Application.configureRouting() {
    routing {
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
}

@Serializable
data class Account(
    val id: String,
    val name: String,
    val ownerId: String
)

@Serializable
data class Entry(
    val id: String,
    val accountId: String,
    val amount: Long,
    val direction: Direction
)

@Serializable
enum class Direction(val value: String) {
    DEBIT("debit"),
    CREDIT("credit");

    override fun toString(): String = value
}

@Serializable
data class CreateAccountRequest(
    val name: String,
    val ownerId: String
)
