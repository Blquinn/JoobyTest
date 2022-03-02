package app

import com.fasterxml.jackson.databind.ObjectMapper
import io.jooby.JoobyTest
import io.jooby.StatusCode
import me.blq.standupapi.App
import me.blq.standupapi.standups.StandupCreateDto
import me.blq.standupapi.standups.StandupDetailDto
import me.blq.standupapi.standups.StandupDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.util.*

@JoobyTest(App::class)
class IntegrationTest(private val serverPort: Int) {
    val baseUrl = "http://localhost:$serverPort"

    companion object {
        val client = HttpClient.newHttpClient()
        val mapper = ObjectMapper().findAndRegisterModules()
    }

    @Test
    fun `get standup by name`() {
        val req = HttpRequest.newBuilder(URI.create("$baseUrl/standups/foo")).build()
        val res = client.send(req, BodyHandlers.ofByteArray())
        assertEquals(StatusCode.OK_CODE, res.statusCode())
        val body = mapper.readValue(res.body(), StandupDetailDto::class.java)
        assertTrue(body.users.isNotEmpty())
//        assertEquals("foobar", body.name)
    }

    @Test
    fun `create standup`() {
        val name = UUID.randomUUID().toString()
        val cr = StandupCreateDto(name)
        val req = HttpRequest
            .newBuilder(URI.create("http://localhost:$serverPort/standups"))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofByteArray(mapper.writeValueAsBytes(cr)))
            .build()

        val res = client.send(req, BodyHandlers.ofByteArray())
        assertEquals(StatusCode.OK_CODE, res.statusCode())
        val body = mapper.readValue(res.body(), StandupDto::class.java)
        assertEquals(cr.name, body.name)
    }
}
