package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.OrderRatesTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

data class ExposedOrderRate(
    val status: String,
    val comment: String
)

class OrderRatesService(database: Database) {
    object OrdersRates : Table(OrderRatesTable.TABLE_NAME) {
        val id = integer(OrderRatesTable.COLUMN_ID).autoIncrement()
        val status = varchar(OrderRatesTable.COLUMN_STATUS, 256)
        val comment = varchar(OrderRatesTable.COLUMN_COMMENT, 256)
    }

    init {
        transaction(database) {
            SchemaUtils.create(OrdersRates)
        }
    }
}