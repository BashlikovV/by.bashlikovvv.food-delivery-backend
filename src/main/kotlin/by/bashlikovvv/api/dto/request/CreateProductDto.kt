package by.bashlikovvv.api.dto.request

import by.bashlikovvv.data.local.dao.ExposedProduct
import by.bashlikovvv.data.local.dao.NutritionFactsService
import by.bashlikovvv.data.local.dao.ProducersService
import by.bashlikovvv.data.local.dao.ProductGroupsService
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProductDto(
    @SerialName("description") val description: String,
    @SerialName("name") val name: String,
    @SerialName("group") val group: Int,
    @SerialName("producer") val producer: Int,
    @SerialName("nutritionFact") val nutritionFact: Int,
) {

    suspend fun toExposedProduct(
        groupsService: ProductGroupsService,
        producersService: ProducersService,
        nutritionFactsService: NutritionFactsService
    ): ExposedProduct {
        return ExposedProduct(
            description = description,
            name = name,
            group = groupsService.read(group) ?: throw RuntimeException("Group not found"),
            producer = producersService.read(producer) ?: throw RuntimeException("Producer not found"),
            nutritionFact = nutritionFactsService.read(nutritionFact) ?: throw RuntimeException("Nutrition facts not found")
        )
    }

}