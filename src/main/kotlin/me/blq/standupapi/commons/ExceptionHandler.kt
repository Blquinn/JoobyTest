package me.blq.standupapi.commons

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import io.jooby.Context
import io.jooby.MediaType
import io.jooby.Router
import io.jooby.StatusCode
import io.jooby.exception.StatusCodeException
import me.blq.standupapi.commons.EntityNotFoundException
import me.blq.standupapi.commons.ValidationErrorDto
import me.blq.standupapi.commons.ValidationErrorResponseDto
import me.blq.standupapi.commons.ValidationException

fun exceptionHandler(
    router: Router,
    objectMapper: ObjectMapper,
): (ctx: Context, cause: Throwable, statusCode: StatusCode) -> Unit {
    return { ctx, cause, statusCode ->
        ctx.setDefaultResponseType(MediaType.json)

        when (cause) {
            is StatusCodeException -> {
                ctx.router.log.warn("Got status code exception: {}", cause.message)
                ctx.responseCode = cause.statusCode
                val res = ValidationErrorResponseDto(cause.localizedMessage, listOf())
                ctx.send(objectMapper.writeValueAsString(res))
            }
            is EntityNotFoundException -> {
                ctx.router.log.info("Object not found: {}", cause.message)
                ctx.responseCode = StatusCode.NOT_FOUND
                val res = ValidationErrorResponseDto(cause.localizedMessage, listOf())
                ctx.send(objectMapper.writeValueAsString(res))
            }
            is MismatchedInputException -> {
                ctx.router.log.info("Request was invalid: {}", cause)
                ctx.responseCode = StatusCode.BAD_REQUEST

                val res = ValidationErrorResponseDto(
                    "Request was invalid", listOf(
                        ValidationErrorDto(".", "Failed to deserialize request body.")
                    )
                )
                ctx.send(objectMapper.writeValueAsString(res))
            }
            is ValueInstantiationException -> {
                ctx.router.log.info("Request was invalid: {}", cause)
                ctx.responseCode = StatusCode.BAD_REQUEST

                val res = ValidationErrorResponseDto(
                    "Request was invalid", listOf(
                        ValidationErrorDto(".", "Failed to deserialize request body.")
                    )
                )
                ctx.send(objectMapper.writeValueAsString(res))
            }
            is ValidationException -> {
                ctx.router.log.info("Request was invalid: {}", cause)
                ctx.responseCode = StatusCode.BAD_REQUEST
                val res = objectMapper.writeValueAsString(cause.validationErrors)
                ctx.send(res)
            }
            else -> {
                router.log.error(
                    "Error handler unexpected error: {} - {}\n{}",
                    statusCode.value(), cause.localizedMessage, cause.stackTrace.joinToString("\n\t")
                )

                ctx.responseCode = StatusCode.SERVER_ERROR
                val res = mapOf("detail" to "An unexpected error occurred.")
                ctx.send(objectMapper.writeValueAsString(res))
            }
        }
    }
}