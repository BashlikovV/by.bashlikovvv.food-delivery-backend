package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.ProductGroupsTable
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

data class ExposedProductGroup(val name: String)

class ProductGroupsService(database: Database) {
    object ProductGroups : Table(ProductGroupsTable.TABLE_NAME) {
        val id = integer(ProductGroupsTable.COLUMN_ID)
        val name = varchar(ProductGroupsTable.COLUMN_NAME, 256)
    }

    init {
        transaction {
            create(ProductGroups)
        }
    }

    suspend fun create(productGroup: ExposedProductGroup): Int =
        dbQuery {
            ProductGroups.insert {
                it[name] = productGroup.name
            }[ProductGroups.id]
        }

    suspend fun readAll(): List<ExposedProductGroup> =
        dbQuery {
            ProductGroups.selectAll()
                .map { ExposedProductGroup(it[ProductGroups.name]) }
        }

    suspend fun read(id: Int): ExposedProductGroup? =
        dbQuery {
            ProductGroups.selectAll()
                .where { ProductGroups.id eq id }
                .map { ExposedProductGroup(it[ProductGroups.name]) }
                .singleOrNull()
        }

    suspend fun update(
        id: Int,
        productGroup: ExposedProductGroup,
    ) {
        dbQuery {
            ProductGroups.update({ ProductGroups.id eq id }) {
                it[name] = productGroup.name
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            ProductGroups.deleteWhere { ProductGroups.id eq id }
        }
    }
}
