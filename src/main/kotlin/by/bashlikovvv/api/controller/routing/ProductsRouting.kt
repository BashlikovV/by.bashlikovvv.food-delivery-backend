package by.bashlikovvv.api.controller.routing

import by.bashlikovvv.api.dto.request.CreateProductDto
import by.bashlikovvv.data.local.dao.NutritionFactsService
import by.bashlikovvv.data.local.dao.ProducersService
import by.bashlikovvv.data.local.dao.ProductGroupsService
import by.bashlikovvv.data.local.dao.ProductsService
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

fun Route.productsRouting() {
    val productsService: ProductsService by inject()

    getProducts(productsService)
    postProduct(
        productsService = productsService,
        groupsService = get(),
        producersService = get(),
        nutritionFactsService = get()
    )
//    postProducts(
//        productsService = productsService,
//        groupsService = get(),
//        producersService = get(),
//        nutritionFactsService = get()
//    )
}

private fun Route.getProducts(productsService: ProductsService) {
    get("/products/") {
        call.respond(Gson().toJson(productsService.readAll()))
    }
}

private fun Route.postProduct(
    productsService: ProductsService,
    groupsService: ProductGroupsService,
    producersService: ProducersService,
    nutritionFactsService: NutritionFactsService
) {
    post("/products/") {
        val createProductDto = call.receive<CreateProductDto>()

        productsService.create(
            createProductDto.toExposedProduct(
                groupsService,
                producersService,
                nutritionFactsService
            ),
            createProductDto.group,
            createProductDto.producer,
            createProductDto.nutritionFact
        )

        call.respond(HttpStatusCode.OK)
    }
}

private fun Route.postProducts(
    productsService: ProductsService,
    groupsService: ProductGroupsService,
    producersService: ProducersService,
    nutritionFactsService: NutritionFactsService
) {
    post("/products/") {
        val createProductDtos = call.receive<List<CreateProductDto>>()

        createProductDtos.forEach { createProductDto ->
            productsService.create(
                createProductDto.toExposedProduct(
                    groupsService,
                    producersService,
                    nutritionFactsService
                ),
                createProductDto.group,
                createProductDto.producer,
                createProductDto.nutritionFact
            )
        }

        call.respond(HttpStatusCode.OK)
    }
}