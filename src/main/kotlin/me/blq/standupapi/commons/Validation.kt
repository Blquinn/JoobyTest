package me.blq.standupapi.commons

import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator


data class ValidationErrorResponseDto(
    val title: String,
    val errors: Collection<ValidationErrorDto>
)

data class ValidationErrorDto(
    val field: String,
    val message: String,
)

class ValidationException(val validationErrors: ValidationErrorResponseDto) : Exception() {
    constructor(title: String) : this(ValidationErrorResponseDto(title, listOf()))
}

class EntityNotFoundException(thing: String) : Exception("$thing was not found.")

fun <T> MutableSet<ConstraintViolation<T>>.toDto(): ValidationErrorResponseDto {
    return ValidationErrorResponseDto("One or more fields were invalid.",
        this.map {
            ValidationErrorDto(it.propertyPath.joinToString("."), it.message)
        }
    )
}

fun Validator.validateRequest(request: Any) {
    val errs = this.validate(request)
    if (errs.isNotEmpty())
        throw ValidationException(errs.toDto())
}
