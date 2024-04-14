package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.StatusesTable
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

data class ExposedStatus(val name: String)

class StatusesService(database: Database) {
    object Statuses : Table(StatusesTable.TABLE_NAME) {
        val id = integer(StatusesTable.COLUMN_ID).autoIncrement()
        val name = varchar(StatusesTable.COLUMN_NAME, 256)
    }

    init {
        transaction(database) {
            create(Statuses)
        }
    }

    suspend fun create(user: ExposedStatus): Int =
        dbQuery {
            Statuses.insert {
                it[name] = user.name
            }[Statuses.id]
        }

    suspend fun readAll(): List<ExposedStatus> {
        return dbQuery {
            Statuses.selectAll()
                .map { ExposedStatus(it[Statuses.name]) }
        }
    }

    suspend fun read(id: Int): ExposedStatus? =
        dbQuery {
            Statuses.selectAll()
                .where { Statuses.id eq id }
                .map { ExposedStatus(it[Statuses.name]) }
                .singleOrNull()
        }

    suspend fun update(
        id: Int,
        status: ExposedStatus,
    ) {
        dbQuery {
            Statuses.update({ Statuses.id eq id }) {
                it[name] = status.name
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Statuses.deleteWhere { Statuses.id eq id }
        }
    }
}
