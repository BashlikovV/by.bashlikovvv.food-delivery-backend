package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.UserTypesTable
import by.bashlikovvv.util.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedUserType(
    val name: String
)

class UserTypesService(database: Database) {
    object UserTypes : Table(UserTypesTable.TABLE_NAME) {
        val id = integer(UserTypesTable.COLUMN_ID).autoIncrement()
        val name = varchar(UserTypesTable.COLUMN_NAME, 256)
    }

    init {
        transaction(database) {
            SchemaUtils.create(UserTypes)
        }
    }

    suspend fun create(userType: ExposedUserType): Int =
        dbQuery {
            UserTypes.insert {
                it[name] = userType.name
            }[UserTypes.id]
        }

    suspend fun read(id: Int): ExposedUserType? =
        dbQuery {
            UserTypes.selectAll()
                .where { UserTypes.id eq id }
                .map { ExposedUserType(it[UserTypes.name]) }
                .singleOrNull()
        }
}