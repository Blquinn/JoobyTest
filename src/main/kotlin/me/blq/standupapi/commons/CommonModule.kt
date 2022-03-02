package me.blq.standupapi.commons

import com.fasterxml.jackson.databind.ObjectMapper
import io.jooby.*
import io.jooby.json.JacksonModule
import jakarta.validation.Validation
import jakarta.validation.Validator

private val thumbsUp = "\uD83D\uDC4D".encodeToByteArray()

/**
 * The Common  Module sets up a Jooby application
 * with various default configurations.
 *
 * Including:
 * 1. An exception handler.
 * 2. Access Logging.
 * 3. OpenAPI modules.
 */
class CommonModule : Extension {
    override fun install(app: Jooby) {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        val validator = Validation.buildDefaultValidatorFactory().validator

        app.services.putIfAbsent(ObjectMapper::class.java, objectMapper)
        app.services.putIfAbsent(Validator::class.java, validator)

        app.install(OpenAPIModule())
        val jacksonModule = JacksonModule(objectMapper)
        app.install(jacksonModule)

        // Register health-check endpoint.
        app.get("/health") {
            it.setResponseHeader("Content-Type", "text/plain; charset=utf-8")
            thumbsUp
        }

        app.decorator(AccessLogHandler())
        app.error(exceptionHandler(app.router, app.require()))
        app.decoder(MediaType.json, validatedJacksonDecoder(jacksonModule, app.require()))
    }
}