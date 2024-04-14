package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.ProducerCountriesTable
import by.bashlikovvv.util.dbQuery
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

data class ExposedProducerCountry(val name: String)

class ProducerCountriesService(database: Database) {
    object ProducerCountries : Table(ProducerCountriesTable.TABLE_NAME) {
        val id = integer(ProducerCountriesTable.COLUMN_ID).autoIncrement()
        val name = varchar(ProducerCountriesTable.COLUMN_NAME, 256)
    }

    init {
        transaction(database) {
            create(ProducerCountries)
        }
    }

    suspend fun create(country: ExposedProducerCountry): Int =
        dbQuery {
            ProducerCountries.insert {
                it[name] = country.name
            }[ProducerCountries.id]
        }

    suspend fun readAll(): List<ExposedProducerCountry> =
        dbQuery {
            ProducerCountries.selectAll()
                .map { ExposedProducerCountry(it[ProducerCountries.name]) }
        }

    suspend fun read(id: Int): ExposedProducerCountry? =
        dbQuery {
            ProducerCountries.selectAll()
                .where { ProducerCountries.id eq id }
                .map { ExposedProducerCountry(it[ProducerCountries.name]) }
                .singleOrNull()
        }

    suspend fun update(
        id: Int,
        producerCountry: ExposedProducerCountry,
    ) {
        dbQuery {
            ProducerCountries.update({ ProducerCountries.id eq id }) {
                it[name] = producerCountry.name
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            ProducerCountries.deleteWhere { ProducerCountries.id eq id }
        }
    }
}
