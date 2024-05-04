package by.bashlikovvv.api.controller.routing

import by.bashlikovvv.api.dto.request.CreateUserDto
import by.bashlikovvv.api.dto.request.toExposedUser
import by.bashlikovvv.data.local.dao.PaymentCartsService
import by.bashlikovvv.data.local.dao.UserAddressService
import by.bashlikovvv.data.local.dao.UsersService
import by.bashlikovvv.util.SecurityUtilsImpl
import by.bashlikovvv.util.getWithCheck
import by.bashlikovvv.util.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.usersRouting() {
    val usersService: UsersService by inject()
    val userAddressService: UserAddressService by inject()
    val paymentCartsService: PaymentCartsService by inject()

    getUser(usersService)
    postUser(usersService, userAddressService, paymentCartsService)
    updateUser(usersService, userAddressService, paymentCartsService)
    checkUser(usersService)
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

private fun Route.postUser(
    usersService: UsersService,
    userAddressService: UserAddressService,
    paymentCartsService: PaymentCartsService
) {
    post("/users/") {
        val createUserDto = call.receive<CreateUserDto>()
        val exposedUser = createUserDto.toExposedUser(
            getAddressById = { id -> userAddressService.read(id) },
            getPaymentCartById = { id -> paymentCartsService.read(id) }
        )

        usersService.create(
            exposedUser,
            typeId = 1,
            addressId = createUserDto.address,
            paymentCartId = createUserDto.paymentCart
        )
    }
}

private fun Route.updateUser(
    usersService: UsersService,
    userAddressService: UserAddressService,
    paymentCartsService: PaymentCartsService
) {
    put("/users/{id?}") {
        val id = call.parameters["id"]?.toInt() ?: return@put call.respond(HttpStatusCode.BadRequest)
        val createUserDto = call.receive<CreateUserDto>()
        val exposedUser = createUserDto.toExposedUser(
            getAddressById = { id -> userAddressService.read(id) },
            getPaymentCartById = { id -> paymentCartsService.read(id) }
        )

        usersService.update(
            id = id,
            user = exposedUser,
            typeId = 1,
            addressId = createUserDto.address,
            paymentCartId = createUserDto.paymentCart
        )
    }
}

// Body: x-www-form-urlencoded - email, password
private fun Route.checkUser(usersService: UsersService) {
    post("/signin") {
        val params = call.receiveParameters()
        val email = params["email"].toString()
        val password = params["password"].toString()
        val user = usersService.read(email)
        if (user != null) {
            val securityUtils = SecurityUtilsImpl()
            val salt = user.salt.split(", ").map { it.toByte() }.toByteArray()
            val hash = securityUtils.passwordToHash(
                password = password.toCharArray(),
                salt = salt
            )

            if (hash.joinToString() == user.hash) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.BadRequest, "incorrect password")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "incorrect email")
        }
    }
}