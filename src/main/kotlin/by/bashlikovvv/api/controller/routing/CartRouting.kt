package by.bashlikovvv.api.controller.routing

import by.bashlikovvv.api.dto.request.CreateCartDto
import by.bashlikovvv.data.local.dao.CartsService
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.cartsRouting() {
    val cartsService: CartsService by inject()

    getCart(cartsService)
    postCart(cartsService)
}

private fun Route.getCart(cartsService: CartsService) {
    get("/carts/{userid?}") {
        val userid = call.parameters["userid"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
        call.respond(HttpStatusCode.OK, Gson().toJson(cartsService.read(userid)))
    }
}

private fun Route.postCart(cartsService: CartsService) {
    post("/carts/") {
        val createCartDto: CreateCartDto = Gson().fromJson(call.receiveText(), CreateCartDto::class.java)
        cartsService.create(createCartDto.cart, createCartDto.amountId, createCartDto.productId, createCartDto.userId)
    }
}