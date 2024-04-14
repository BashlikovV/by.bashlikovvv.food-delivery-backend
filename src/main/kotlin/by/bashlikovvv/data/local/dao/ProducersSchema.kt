package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.ProducersTable
import by.bashlikovvv.util.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

data class ExposedProducer(
    val name: String,
    val country: ExposedProducerCountry
)

class ProducersService(database: Database) {
    object Producers : Table(ProducersTable.TABLE_NAME) {
        val id = integer(ProducersTable.COLUMN_ID).autoIncrement()
        val name = varchar(ProducersTable.COLUMN_NAME, 256)
        val country = integer(ProducersTable.COLUMN_PRODUCER_COUNTRY_FK).references(ProducerCountriesService.ProducerCountries.id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Producers)
        }
    }

    suspend fun create(producer: ExposedProducer, countryId: Int): Int =
        dbQuery {
            Producers.insert {
                it[name] = producer.name
                it[country] = countryId
            }[Producers.id]
        }

    suspend fun read(id: Int): ExposedProducer? =
        dbQuery {
            val country = Join(
                table = Producers,
                otherTable = ProducerCountriesService.ProducerCountries,
                joinType = JoinType.FULL,
                additionalConstraint = { Producers.country eq ProducerCountriesService.ProducerCountries.id }
            )
                .selectAll()
                .map { ExposedProducerCountry(it[ProducerCountriesService.ProducerCountries.name]) }
                .firstOrNull() ?: return@dbQuery null
            Producers.selectAll()
                .where { Producers.id eq id }
                .map {
                    ExposedProducer(
                        name = it[Producers.name],
                        country = country
                    )
                }
                .singleOrNull()
        }

    suspend fun update(
        id: Int,
        exposedProducer: ExposedProducer,
        countryId: Int?
    ) {
        dbQuery {
            Producers.update({ Producers.id eq id }) {
                it[name] = exposedProducer.name
                if (countryId != null) {
                    it[country] = countryId
                }
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Producers.deleteWhere { Producers.id eq id }
        }
    }
}