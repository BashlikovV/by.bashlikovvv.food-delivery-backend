package by.bashlikovvv.api.controller

import by.bashlikovvv.api.controller.routing.productsRouting
import by.bashlikovvv.api.controller.routing.usersRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.koin.ktor.ext.get

fun Application.configureRouting() {
    val database: Database = get()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        usersRouting()
        productsRouting()
    }
}
