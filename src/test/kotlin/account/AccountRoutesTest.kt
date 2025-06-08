package account

import com.adriano.account.Account
import com.adriano.account.CreateAccountRequest
import com.adriano.module
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class AccountRoutesTest {

    @Test
    fun testCreateAccount() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val payload = CreateAccountRequest(name = "Cash Account", ownerId = "user-123")

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
    fun testListAllAccounts() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // given an account
        val newAccount = CreateAccountRequest(name = "Cash Account", ownerId = UUID.randomUUID().toString())

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
    fun testGetAccountInvalidId() = testApplication {
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
    fun testGetAccountValidId() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // given an account
        val newAccount = CreateAccountRequest(name = "Cash Account", ownerId = UUID.randomUUID().toString())
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
    fun testAccountBalance() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // given an account
        val newAccount = CreateAccountRequest(name = "Cash Account", ownerId = UUID.randomUUID().toString())
        val newAccountResponse = client.post("/account") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(newAccount))
        }
        assertEquals(HttpStatusCode.Created, newAccountResponse.status)

        // when
        val id = newAccountResponse.body<Account>().id
        val response = client.get("/account/$id/balance") {
            accept(ContentType.Application.Json)
        }

        // then
        assertEquals(HttpStatusCode.NotImplemented, response.status)
    }
}
