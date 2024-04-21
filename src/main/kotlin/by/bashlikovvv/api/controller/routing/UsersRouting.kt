package by.bashlikovvv.api.controller.routing

import by.bashlikovvv.data.local.dao.UsersService
import by.bashlikovvv.util.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.usersRouting() {
    val usersService: UsersService by inject()

    getUser(usersService)
}

private fun Route.getUser(usersService: UsersService) {
    get("/users/{id?}") {
        val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val user = usersService.read(id)

        call.respond(
            isCorrect = { user != null },
            onCorrect = {
                HttpStatusCode.OK to user!!
            },
            onIncorrect = {
                HttpStatusCode.BadRequest to ""
            }
        )
    }
}