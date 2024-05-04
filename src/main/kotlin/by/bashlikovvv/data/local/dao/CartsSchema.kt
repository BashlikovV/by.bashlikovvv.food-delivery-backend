package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.CartsTable
import by.bashlikovvv.util.dbQuery
import org.jetbrains.exposed.sql.*

data class ExposedCart(
    val size: Int,
    val items: String,
    val amount: ExposedAmount,
    val product: ExposedProduct
)

class CartsService(database: Database) {
    object Carts : Table(CartsTable.TABLE_NAME) {
        val id = integer(CartsTable.COLUMN_ID)
        val size = integer(CartsTable.COLUMN_SIZE)
        val items = varchar(CartsTable.COLUMN_ITEMS, 256)
        val amount = integer(CartsTable.COLUMN_CART_AMOUNT_FK).references(AmountsService.Amounts.id)
        val product = integer(CartsTable.COLUMN_CART_PRODUCT_FK).references(ProductsService.Products.id)
        val user = integer(CartsTable.COLUMN_CART_USER_FK).references(UsersService.Users.id)
    }

    suspend fun create(
        cart: ExposedCart,
        amountId: Int,
        productId: Int,
        userId: Int
    ): Int =
        dbQuery {
            Carts.insert {
                it[id] = (Carts.selectAll().maxOfOrNull { it[id] } ?: 0) + 1
                it[size] = cart.size
                it[items] = cart.items
                it[amount] = amountId
                it[product] = productId
                it[user] = userId
            }[Carts.id]
        }

    suspend fun read(id: Int): List<ExposedCart> =
        dbQuery {
            val amount = Join(
                table = Carts,
                otherTable = AmountsService.Amounts,
                joinType = JoinType.FULL,
                additionalConstraint = { Carts.amount eq AmountsService.Amounts.id }
            )
                .selectAll()
                .map { amountDbo ->
                    ExposedAmount(
                        delivery = amountDbo[AmountsService.Amounts.delivery],
                        discount = amountDbo[AmountsService.Amounts.discount],
                        amount = amountDbo[AmountsService.Amounts.amount]
                    )
                }.firstOrNull() ?: return@dbQuery listOf()

            val group = Join(
                table = ProductsService.Products,
                otherTable = ProductGroupsService.ProductGroups,
                joinType = JoinType.FULL,
                additionalConstraint = { ProductsService.Products.group eq ProductGroupsService.ProductGroups.id }
            )
                .selectAll()
                .map {
                    ExposedProductGroup(it[ProductGroupsService.ProductGroups.name])
                }
                .firstOrNull() ?: return@dbQuery listOf()
            val country = Join(
                table = ProducersService.Producers,
                otherTable = ProducerCountriesService.ProducerCountries,
                joinType = JoinType.FULL,
                additionalConstraint = { ProducersService.Producers.country eq ProducerCountriesService.ProducerCountries.id }
            )
                .selectAll()
                .map { ExposedProducerCountry(it[ProducerCountriesService.ProducerCountries.name]) }
                .firstOrNull() ?: return@dbQuery listOf()
            val producer = Join(
                table = ProductsService.Products,
                otherTable = ProducersService.Producers,
                joinType = JoinType.FULL,
                additionalConstraint = { ProductsService.Products.producer eq ProducersService.Producers.id }
            )
                .selectAll()
                .map {
                    ExposedProducer(
                        name = it[ProducersService.Producers.name],
                        country = country
                    )
                }
                .firstOrNull() ?: return@dbQuery listOf()
            val nutritionFact = Join(
                table = ProductsService.Products,
                otherTable = NutritionFactsService.NutritionFacts,
                joinType = JoinType.FULL,
                additionalConstraint = { ProductsService.Products.nutritionFact eq NutritionFactsService.NutritionFacts.id }
            )
                .selectAll()
                .map {
                    ExposedNutritionFact(
                        proteins = it[NutritionFactsService.NutritionFacts.proteins],
                        lipids = it[NutritionFactsService.NutritionFacts.lipids],
                        glucides = it[NutritionFactsService.NutritionFacts.glucides],
                        calories = it[NutritionFactsService.NutritionFacts.calories]
                    )
                }
                .firstOrNull() ?: return@dbQuery listOf()
            Carts.selectAll()
                .where { Carts.user eq id }
                .map {
                    val product = Join(
                        table = Carts,
                        otherTable = ProductsService.Products,
                        joinType = JoinType.FULL,
                        additionalConstraint = { Carts.product eq ProductsService.Products.id }
                    )
                        .selectAll()
                        .where { ProductsService.Products.id eq it[Carts.product] }
                        .map { productDbo ->
                            ExposedProduct(
                                id = productDbo[ProductsService.Products.id],
                                description = productDbo[ProductsService.Products.description],
                                name = productDbo[ProductsService.Products.name],
                                group = group,
                                producer = producer,
                                nutritionFact = nutritionFact
                            )
                        }
                        .firstOrNull() ?: return@dbQuery listOf()

                    ExposedCart(
                        size = it[Carts.size],
                        items = it[Carts.items],
                        amount = amount,
                        product = product
                    )
                }
        }
}