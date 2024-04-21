package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.PaymentCartsTable
import by.bashlikovvv.util.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedPaymentCart(
    val number: String,
    val system: String,
    val default: String,
    val email: String
)

class PaymentCartsService(database: Database) {
    object PaymentCarts : Table(PaymentCartsTable.TABLE_NAME) {
        val id = integer(PaymentCartsTable.COLUMN_ID).autoIncrement()
        val number = varchar(PaymentCartsTable.COLUMN_NUMBER, 256)
        val system = varchar(PaymentCartsTable.COLUMN_SYSTEM, 256)
        val default = varchar(PaymentCartsTable.COLUMN_DEFAULT, 256)
        val email = varchar(PaymentCartsTable.COLUMN_EMAIL, 256)
    }

    init {
        transaction(database) {
            SchemaUtils.create(PaymentCarts)
        }
    }

    suspend fun create(paymentCart: ExposedPaymentCart): Int = dbQuery {
        PaymentCarts.insert {
            it[number] = paymentCart.number
            it[system] = paymentCart.system
            it[default] = paymentCart.default
            it[email] = paymentCart.email
        }[PaymentCarts.id]
    }

    suspend fun delete(id: Int) {
        dbQuery {
            PaymentCarts.deleteWhere { PaymentCarts.id eq id }
        }
    }
}