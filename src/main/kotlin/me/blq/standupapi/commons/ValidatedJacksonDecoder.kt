package me.blq.standupapi.commons

import io.jooby.MessageDecoder
import io.jooby.json.JacksonModule
import jakarta.validation.Validator

fun validatedJacksonDecoder(jacksonModule: JacksonModule, validator: Validator): MessageDecoder {
    return MessageDecoder { ctx, type ->
        val obj = jacksonModule.decode(ctx, type)
        validator.validateRequest(obj)
        obj
    }
}