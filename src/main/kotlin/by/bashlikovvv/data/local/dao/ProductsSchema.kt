package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.ProductsTable
import by.bashlikovvv.util.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

data class ExposedProduct(
    val description: String,
    val name: String,
    val group: ExposedProductGroup,
    val producer: ExposedProducer,
    val nutritionFact: ExposedNutritionFact
)

class ProductsService(database: Database) {
    object Products : Table(ProductsTable.TABLE_NAME) {
        val id = integer(ProductsTable.COLUMN_ID).autoIncrement()
        val description = varchar(ProductsTable.COLUMN_DESCRIPTION, 256)
        val name = varchar(ProductsTable.COLUMN_NAME, 256)
        val group = integer(ProductsTable.COLUMN_PRODUCT_GROUP_FK).references(ProductGroupsService.ProductGroups.id)
        val producer = integer(ProductsTable.COLUMN_PRODUCT_PRODUCER_FK).references(ProducersService.Producers.id)
        val nutritionFact = integer(ProductsTable.COLUMN_PRODUCT_NUTRITION_FACT_FK).references(NutritionFactsService.NutritionFacts.id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Products)
        }
    }

    suspend fun create(
        exposedProduct: ExposedProduct,
        groupId: Int,
        producerId: Int,
        nutritionFactId: Int
    ): Int =
        dbQuery {
            Products.insert {
                it[id] = (Products.selectAll().maxOfOrNull { it[id] } ?: 0) + 1
                it[description] = exposedProduct.description
                it[name] = exposedProduct.name
                it[group] = groupId
                it[producer] = producerId
                it[nutritionFact] = nutritionFactId
            }[Products.id]
        }

    suspend fun readAll(): List<ExposedProduct> =
        dbQuery {
            Products.selectAll()
                .map {
                    val groupId = it[Products.group]
                    val producerId = it[Products.producer]
                    val nutritionFactId = it[Products.nutritionFact]
                    val group = Join(
                        table = Products,
                        otherTable = ProductGroupsService.ProductGroups,
                        joinType = JoinType.FULL,
                        additionalConstraint = { Products.group eq ProductGroupsService.ProductGroups.id }
                    )
                        .selectAll()
                        .where { ProductGroupsService.ProductGroups.id eq groupId }
                        .map {
                            ExposedProductGroup(it[ProductGroupsService.ProductGroups.name])
                        }
                        .firstOrNull() ?: return@dbQuery listOf()
                    val producer = Join(
                        table = Products,
                        otherTable = ProducersService.Producers,
                        joinType = JoinType.FULL,
                        additionalConstraint = { Products.producer eq ProducersService.Producers.id }
                    )
                        .selectAll()
                        .where { ProducersService.Producers.id eq producerId }
                        .map {
                            val countryId = it[ProducersService.Producers.country]
                            val country = Join(
                                table = ProducersService.Producers,
                                otherTable = ProducerCountriesService.ProducerCountries,
                                joinType = JoinType.FULL,
                                additionalConstraint = { ProducersService.Producers.country eq ProducerCountriesService.ProducerCountries.id }
                            )
                                .selectAll()
                                .where { ProducersService.Producers.country eq countryId }
                                .map { ExposedProducerCountry(it[ProducerCountriesService.ProducerCountries.name]) }
                                .firstOrNull() ?: return@dbQuery listOf()

                            ExposedProducer(
                                name = it[ProducersService.Producers.name],
                                country = country
                            )
                        }
                        .firstOrNull() ?: return@dbQuery listOf()
                    val nutritionFact = Join(
                        table = Products,
                        otherTable = NutritionFactsService.NutritionFacts,
                        joinType = JoinType.FULL,
                        additionalConstraint = { Products.nutritionFact eq NutritionFactsService.NutritionFacts.id }
                    )
                        .selectAll()
                        .where { NutritionFactsService.NutritionFacts.id eq nutritionFactId }
                        .map {
                            ExposedNutritionFact(
                                proteins = it[NutritionFactsService.NutritionFacts.proteins],
                                lipids = it[NutritionFactsService.NutritionFacts.lipids],
                                glucides = it[NutritionFactsService.NutritionFacts.glucides],
                                calories = it[NutritionFactsService.NutritionFacts.calories]
                            )
                        }
                        .firstOrNull() ?: return@dbQuery listOf()

                    ExposedProduct(
                        description = it[Products.description],
                        name = it[Products.name],
                        group = group,
                        producer = producer,
                        nutritionFact = nutritionFact
                    )
                }
        }

    suspend fun read(id: Int): ExposedProduct? =
        dbQuery {
            val group = Join(
                table = Products,
                otherTable = ProductGroupsService.ProductGroups,
                joinType = JoinType.FULL,
                additionalConstraint = { Products.group eq ProductGroupsService.ProductGroups.id }
            )
                .selectAll()
                .map {
                    ExposedProductGroup(it[ProductGroupsService.ProductGroups.name])
                }
                .firstOrNull() ?: return@dbQuery null
            val country = Join(
                table = ProducersService.Producers,
                otherTable = ProducerCountriesService.ProducerCountries,
                joinType = JoinType.FULL,
                additionalConstraint = { ProducersService.Producers.country eq ProducerCountriesService.ProducerCountries.id }
            )
                .selectAll()
                .map { ExposedProducerCountry(it[ProducerCountriesService.ProducerCountries.name]) }
                .firstOrNull() ?: return@dbQuery null
            val producer = Join(
                table = Products,
                otherTable = ProducersService.Producers,
                joinType = JoinType.FULL,
                additionalConstraint = { Products.producer eq ProducersService.Producers.id }
            )
                .selectAll()
                .map {
                    ExposedProducer(
                        name = it[ProducersService.Producers.name],
                        country = country
                    )
                }
                .firstOrNull() ?: return@dbQuery null
            val nutritionFact = Join(
                table = Products,
                otherTable = NutritionFactsService.NutritionFacts,
                joinType = JoinType.FULL,
                additionalConstraint = { Products.nutritionFact eq NutritionFactsService.NutritionFacts.id }
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
                .firstOrNull() ?: return@dbQuery null
            Products.selectAll()
                .where { Products.id eq id }
                .map {
                    ExposedProduct(
                        description = it[Products.description],
                        name = it[Products.name],
                        group = group,
                        producer = producer,
                        nutritionFact = nutritionFact
                    )
                }
                .singleOrNull()
        }

    suspend fun update(
        id: Int,
        product: ExposedProduct,
        groupId: Int?,
        producerId: Int?,
        nutritionFactId: Int?
    ) {
        dbQuery {
            Products.update({ Products.id eq id }) {
                it[description] = product.name
                it[name] = product.name
                if (groupId != null) {
                    it[group] = groupId
                }
                if (producerId != null) {
                    it[producer] = producerId
                }
                if (nutritionFactId != null) {
                    it[nutritionFact] = nutritionFactId
                }
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Products.deleteWhere { Products.id eq id }
        }
    }
}