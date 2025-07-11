package com.adriano.ledger.transaction

import com.adriano.ledger.account.AccountService
import com.adriano.ledger.module
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.*

class TransactionRoutesTest {

    private lateinit var accountService: AccountService
    private lateinit var transactionService: TransactionService

    @BeforeTest
    fun init() {
        transactionService = TransactionService()
        accountService = AccountService(transactionService)
    }

    @Test
    fun `should succeed when the request is valid`() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val johnAccount = accountService.createAccount("John", UUID.randomUUID())
        val milanAccount = accountService.createAccount("Milan", UUID.randomUUID())
        val amount = 5000L
        val payload = CreateTransactionRequest(
            description = "John sent 50 EUR to Milan",
            timestamp = Clock.System.now(),
            entries = listOf(
                Entry(
                    accountId = johnAccount.id,
                    amount = amount,
                    direction = Direction.DEBIT
                ),
                Entry(
                    accountId = milanAccount.id,
                    amount = amount,
                    direction = Direction.CREDIT
                )
            )
        )

        val response = client.post("/transaction") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body = response.body<Transaction>()
        assertNotNull(body.id)
        assertTrue { body.createdAt >= payload.timestamp }
        val expected = Transaction(
            id = body.id,
            description = payload.description,
            timestamp = payload.timestamp,
            createdAt = body.createdAt,
            entries = payload.entries
        )

        assertEquals(expected, body)
    }
}