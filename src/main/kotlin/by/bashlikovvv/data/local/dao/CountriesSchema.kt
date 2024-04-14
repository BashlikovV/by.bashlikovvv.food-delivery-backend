package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.CountriesTable
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

data class ExposedCountry(val name: String)

class CountriesService(database: Database) {
    object Countries : Table(CountriesTable.TABLE_NAME) {
        val id = integer(CountriesTable.COLUMN_ID).autoIncrement()
        val name = varchar(CountriesTable.COLUMN_NAME, 256)
    }

    init {
        transaction(database) {
            create(Countries)
        }
    }

    suspend fun create(country: ExposedCountry): Int =
        dbQuery {
            Countries.insert {
                it[name] = country.name
            }[Countries.id]
        }

    suspend fun readAll(): List<ExposedCountry> =
        dbQuery {
            Countries.selectAll()
                .map { ExposedCountry(it[Countries.name]) }
        }

    suspend fun read(id: Int): ExposedCountry? =
        dbQuery {
            Countries.selectAll()
                .where { Countries.id eq id }
                .map { ExposedCountry(it[Countries.name]) }
                .singleOrNull()
        }

    suspend fun update(
        id: Int,
        country: ExposedCountry,
    ) {
        dbQuery {
            Countries.update({ Countries.id eq id }) {
                it[name] = country.name
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Countries.deleteWhere { Countries.id eq id }
        }
    }
}
