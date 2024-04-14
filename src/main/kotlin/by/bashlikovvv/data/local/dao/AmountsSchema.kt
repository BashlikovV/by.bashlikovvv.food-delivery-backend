package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.AmountsTable
import by.bashlikovvv.util.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

data class ExposedAmount(
    val delivery: String,
    val discount: String,
    val amount: String
)

class AmountsService(database: Database) {
    object Amounts : Table(AmountsTable.TABLE_NAME) {
        val id = integer(AmountsTable.COLUMN_ID).autoIncrement()
        val delivery = varchar(AmountsTable.COLUMN_DELIVERY, 256)
        val discount = varchar(AmountsTable.COLUMN_DISCOUNT, 256)
        val amount = varchar(AmountsTable.COLUMN_AMOUNT, 256)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Amounts)
        }
    }

    suspend fun create(amount: ExposedAmount): Int =
        dbQuery {
            Amounts.insert {
                it[delivery] = amount.delivery
                it[discount] = amount.discount
                it[this.amount] = amount.amount
            }[Amounts.id]
        }

    suspend fun read(id: Int): ExposedAmount? =
        dbQuery {
            Amounts.selectAll()
                .where { Amounts.id eq id }
                .map {
                    ExposedAmount(
                        delivery = it[Amounts.delivery],
                        discount = it[Amounts.discount],
                        amount = it[Amounts.amount]
                    )
                }
                .singleOrNull()
        }

    suspend fun update(id: Int, amount: ExposedAmount) {
        dbQuery {
            Amounts.update({ Amounts.id eq id }) {
                it[delivery] = amount.delivery
                it[discount] = amount.discount
                it[this.amount] = amount.amount
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Amounts.deleteWhere { Amounts.id eq id }
        }
    }
}