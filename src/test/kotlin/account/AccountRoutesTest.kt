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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


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

        var response = client.get("/account") {
            accept(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val listOfAccounts = response.body<List<Account>>()
        assertEquals(emptyList(), listOfAccounts)

        val newAccount = CreateAccountRequest(name = "Cash Account", ownerId = "user-123")

        val createAccResponse = client.post("/account") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(newAccount))
        }
        assertEquals(HttpStatusCode.Created, createAccResponse.status)

        response = client.get("/account") {
            accept(ContentType.Application.Json)
        }

        val actual = response.body<List<Account>>()
        assertEquals(1, actual.size)
        assertEquals(newAccount.name, actual.first().name)
        assertEquals(newAccount.ownerId, actual.first().ownerId)
    }
}
