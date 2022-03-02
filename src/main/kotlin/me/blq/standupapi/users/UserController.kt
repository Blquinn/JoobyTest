package me.blq.standupapi.users

import io.jooby.annotations.GET
import io.jooby.annotations.POST
import io.jooby.annotations.Path
import io.jooby.annotations.PathParam
import io.swagger.v3.oas.annotations.tags.Tag

@Path("/users")
@Tag(name = "Users")
class UserController(
    private val userService: UserService
) {
    @GET("/{id}")
    fun getById(@PathParam id: Long): UserDto =
        userService.getById(id)

    @POST
    fun post(dto: UserCreateDto): UserDto =
        userService.create(dto)
}
