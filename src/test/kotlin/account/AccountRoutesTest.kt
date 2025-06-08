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
}
