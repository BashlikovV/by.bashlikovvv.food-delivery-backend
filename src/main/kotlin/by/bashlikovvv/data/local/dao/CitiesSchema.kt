package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.CitiesTable
import by.bashlikovvv.data.local.contract.PsqlContract.CountriesTable
import by.bashlikovvv.util.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

data class ExposedCity(
    val name: String,
    val timeZone: Int,
    val country: ExposedCountry?,
)

class CitiesService(database: Database) {
    object Cities : Table(CitiesTable.TABLE_NAME) {
        val id = integer(CitiesTable.COLUMN_ID).autoIncrement()
        val name = varchar(CitiesTable.COLUMN_NAME, 256)
        val timeZone = integer(CitiesTable.COLUMN_TIME_ZONE)
        val country = integer(CitiesTable.COLUMN_CITIES_COUNTRIES_FK).references(CountriesService.Countries.id)
    }

    init {
        transaction(database) {
            create(Cities)
        }
    }

    suspend fun create(
        city: ExposedCity,
        countryId: Int,
    ): Int =
        dbQuery {
            Cities.insert {
                it[name] = city.name
                it[timeZone] = city.timeZone
                it[country] = countryId
            }[Cities.id]
        }

    suspend fun read(id: Int): ExposedCity? =
        dbQuery {
            val join = Join(
                table = Cities,
                otherTable = CountriesService.Countries,
                joinType = JoinType.FULL,
                additionalConstraint = { CountriesService.Countries.id eq Cities.country },
            )
            Cities.selectAll()
                .where { Cities.id eq id }
                .map { cityDto ->
                    ExposedCity(
                        name = cityDto[Cities.name],
                        timeZone = cityDto[Cities.timeZone],
                        country = join.selectAll()
                            .map { ExposedCountry(it[CountriesService.Countries.name]) }
                            .firstOrNull(),
                    )
                }.singleOrNull()
        }

    suspend fun update(
        id: Int,
        city: ExposedCity,
        countryId: Int?,
    ) {
        dbQuery {
            Cities.update {
                it[name] = city.name
                it[timeZone] = city.timeZone
                countryId ?: return@update
                it[country] = countryId
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Cities.deleteWhere { Cities.id eq id }
        }
    }
}
