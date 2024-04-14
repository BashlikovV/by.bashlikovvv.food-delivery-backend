package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.NutritionFactsTable
import by.bashlikovvv.util.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

import org.jetbrains.exposed.sql.transactions.transaction

data class ExposedNutritionFact(
    val proteins: Float,
    val lipids: Float,
    val glucides: Float,
    val calories: Float
)

class NutritionFactsService(database: Database) {
    object NutritionFacts : Table(NutritionFactsTable.TABLE_NAME) {
        val id = integer(NutritionFactsTable.COLUMN_ID).autoIncrement()
        val proteins = float(NutritionFactsTable.COLUMN_PROTEINS)
        val lipids = float(NutritionFactsTable.COLUMN_LIPIDS)
        val glucides = float(NutritionFactsTable.COLUMN_GLUCIDES)
        val calories = float(NutritionFactsTable.COLUMN_CALORIES)
    }

    init {
        transaction(database) {
            SchemaUtils.create(NutritionFacts)
        }
    }

    suspend fun create(nutritionFact: ExposedNutritionFact): Int =
        dbQuery {
            NutritionFacts.insert {
                it[proteins] = nutritionFact.proteins
                it[lipids] = nutritionFact.lipids
                it[glucides] = nutritionFact.glucides
                it[calories] = nutritionFact.calories
            }[NutritionFacts.id]
        }

    suspend fun readAll(): List<ExposedNutritionFact> =
        dbQuery {
            NutritionFacts.selectAll()
                .map {
                    ExposedNutritionFact(
                        proteins = it[NutritionFacts.proteins],
                        lipids = it[NutritionFacts.lipids],
                        glucides = it[NutritionFacts.glucides],
                        calories = it[NutritionFacts.calories],
                    )
                }
        }

    suspend fun read(id: Int): ExposedNutritionFact? =
        dbQuery {
            NutritionFacts.selectAll()
                .where { NutritionFacts.id eq id }
                .map {
                    ExposedNutritionFact(
                        proteins = it[NutritionFacts.proteins],
                        lipids = it[NutritionFacts.lipids],
                        glucides = it[NutritionFacts.glucides],
                        calories = it[NutritionFacts.calories],
                    )
                }.singleOrNull()
        }

    suspend fun update(id: Int, nutritionFact: ExposedNutritionFact) {
        dbQuery {
            NutritionFacts.update({ NutritionFacts.id eq id }) {
                it[proteins] = nutritionFact.proteins
                it[lipids] = nutritionFact.lipids
                it[glucides] = nutritionFact.glucides
                it[calories] = nutritionFact.calories
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            NutritionFacts.deleteWhere { NutritionFacts.id eq id }
        }
    }
}