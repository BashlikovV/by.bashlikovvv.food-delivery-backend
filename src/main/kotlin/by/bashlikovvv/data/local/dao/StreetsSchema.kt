package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.StreetsTable
import by.bashlikovvv.util.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedStreet(val name: String)

class StreetsService(database: Database) {
    object Streets : Table(StreetsTable.TABLE_NAME) {
        val id = integer(StreetsTable.COLUMN_ID).autoIncrement()
        val name = varchar(StreetsTable.COLUMN_NAME, 256)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Streets)
        }
    }

    suspend fun create(street: ExposedStreet): Int =
        dbQuery {
            Streets.insert {
                it[name] = street.name
            }[Streets.id]
        }

    suspend fun read(id: Int): ExposedStreet? =
        dbQuery {
            Streets.selectAll()
                .where { Streets.id eq id }
                .map { ExposedStreet(it[Streets.name]) }
                .singleOrNull()
        }

    suspend fun update(id: Int, street: ExposedStreet) {
        dbQuery {
            Streets.update({ Streets.id eq id }) {
                it[name] = street.name
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Streets.deleteWhere { Streets.id eq id }
        }
    }
}