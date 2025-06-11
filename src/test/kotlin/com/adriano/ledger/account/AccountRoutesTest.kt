package com.adriano.ledger.account

import com.adriano.ledger.module
import com.adriano.ledger.transaction.CreateTransactionRequest
import com.adriano.ledger.transaction.Direction
import com.adriano.ledger.transaction.Entry
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class AccountRoutesTest {

    @Test
    fun `should create an account when request is valid`() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val payload = CreateAccountRequest(name = "Cash Account", ownerId = UUID.randomUUID())
        val response = client.post("/account") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(payload))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val createdAccount = response.body<Account>()
        assertEquals(payload.name, createdAccount.name)
        assertEquals(payload.ownerId, createdAccount.ownerId)
        assertNotNull(createdAccount.id)
    }

    @Test
    fun `should list all accounts`() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // given an account
        val newAccount = CreateAccountRequest(name = "Cash Account", ownerId = UUID.randomUUID())

        val createAccResponse = client.post("/account") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(newAccount))
        }
        assertEquals(HttpStatusCode.Created, createAccResponse.status)

        // when
        val response = client.get("/account") {
            accept(ContentType.Application.Json)
        }

        // then
        val accounts = response.body<List<Account>>()
        assertTrue { accounts.isNotEmpty() }
        val account = accounts.find { it.ownerId == newAccount.ownerId }
        assertNotNull(account)
        assertEquals(newAccount.name, account.name)
        assertEquals(newAccount.ownerId, account.ownerId)
        assertNotNull(account.id)
    }

    @Test
    fun `should fail when id is invalid`() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // when
        val response = client.get("/account/invalid-123") {
            accept(ContentType.Application.Json)
        }

        // then
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid UUID format", response.body())
    }

    @Test
    fun `should return the account when id is given`() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // given an account
        val newAccount = CreateAccountRequest(name = "Cash Account", ownerId = UUID.randomUUID())
        val newAccountResponse = client.post("/account") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(newAccount))
        }
        assertEquals(HttpStatusCode.Created, newAccountResponse.status)

        // when
        val id = newAccountResponse.body<Account>().id
        val response = client.get("/account/$id") {
            accept(ContentType.Application.Json)
        }

        // then
        assertEquals(HttpStatusCode.OK, response.status)
        val expected = Account(id = id, name = newAccount.name, ownerId = newAccount.ownerId)
        assertEquals(expected, response.body<Account>())
    }

    @Test
    fun `should calculate balance`() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // given a john account
        val johnUserId = UUID.randomUUID()
        val johnAccResp = client.post("/account") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateAccountRequest(name = "Cash Account", ownerId = johnUserId)))
        }
        assertEquals(HttpStatusCode.Created, johnAccResp.status)
        val johnAccId = johnAccResp.body<Account>().id

        // and a milan account
        val milanUserId = UUID.randomUUID()
        val milanAccResp = client.post("/account") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateAccountRequest(name = "Cash Account", ownerId = milanUserId)))
        }
        assertEquals(HttpStatusCode.Created, milanAccResp.status)
        val milanAccount = milanAccResp.body<Account>()

        // and john sent money to milan
        val payload1 = CreateTransactionRequest(
            description = "John sent 100 EUR to Milan",
            timestamp = Clock.System.now(),
            entries = listOf(
                Entry(
                    accountId = johnAccId,
                    amount = 10000L,
                    direction = Direction.DEBIT
                ),
                Entry(
                    accountId = milanAccount.id,
                    amount = 10000L,
                    direction = Direction.CREDIT
                )
            )
        )
        val response1 = client.post("/transaction") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(payload1))
        }
        assertEquals(HttpStatusCode.Created, response1.status)

        // and milan sent money to john
        val payload2 = CreateTransactionRequest(
            description = "Milan sent 50 EUR to John",
            timestamp = Clock.System.now(),
            entries = listOf(
                Entry(
                    accountId = milanAccount.id,
                    amount = 5000L,
                    direction = Direction.DEBIT
                ),
                Entry(
                    accountId = johnAccId,
                    amount = 5000L,
                    direction = Direction.CREDIT
                )
            )
        )
        val response2 = client.post("/transaction") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(payload2))
        }
        assertEquals(HttpStatusCode.Created, response2.status)

        // when calculate john balance
        val response = client.get("/account/${milanAccount.id}/balance") {
            accept(ContentType.Application.Json)
        }

        // then
        assertEquals(HttpStatusCode.OK, response.status)
        val balanceResult = response.body<AccountBalanceResponse>()
        val expectedBalance = AccountBalanceResponse(
            account = milanAccount,
            balanceInCents = 5000L
        )
        assertEquals(expectedBalance, balanceResult)
    }
}
