package me.blq.standupapi

import io.jooby.Kooby
import io.jooby.hibernate.HibernateModule
import io.jooby.hikari.HikariModule
import io.jooby.require
import io.jooby.runApp
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import me.blq.standupapi.commons.CommonModule
import me.blq.standupapi.standups.StandupController
import me.blq.standupapi.standups.StandupRepositoryImpl
import me.blq.standupapi.standups.StandupService
import me.blq.standupapi.users.UserController
import me.blq.standupapi.users.UserRepositoryImpl
import me.blq.standupapi.users.UserService

@OpenAPIDefinition(
    info = Info(
        title = "Standups API",
        description = "An API that manages standup.",
        contact = Contact(
            name = "Benjamin Quinn",
            url = "https://blq.me",
            email = "i@blq.me"
        ),
        version = "0.1"
    )
)
class App : Kooby({
    install(CommonModule())
    install(HikariModule())
    install(HibernateModule())

    val standupRepository = StandupRepositoryImpl()
    val userRepository = UserRepositoryImpl()
    val standupService = StandupService(require(), standupRepository)
    val userService = UserService(require(), userRepository)

    mvc(StandupController(standupService))
    mvc(UserController(userService))
})

fun main(args: Array<String>) {
    runApp(args, App::class)
}
