package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.OrderStatuses
import by.bashlikovvv.util.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

data class ExposedOrderStatus(
    val lastUpdate: Long,
    val status: ExposedStatus
)

class OrderStatusesService(database: Database) {
    object OrdersStatuses : Table(OrderStatuses.TABLE_NAME) {
        val id = integer(OrderStatuses.COLUMN_ID).autoIncrement()
        val lastUpdate = long(OrderStatuses.COLUMN_LAST_UPDATE)
        val status = integer(OrderStatuses.COLUMN_ORDER_STATUS_STATUS_FK).references(StatusesService.Statuses.id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(OrdersStatuses)
        }
    }

    suspend fun create(
        orderStatus: ExposedOrderStatus,
        statusId: Int
    ): Int =
        dbQuery {
            OrdersStatuses.insert {
                it[lastUpdate] = orderStatus.lastUpdate
                it[status] = statusId
            }[OrdersStatuses.id]
        }

    suspend fun read(id: Int): ExposedOrderStatus? =
        dbQuery {
            val status = Join(
                table = OrdersStatuses,
                otherTable = StatusesService.Statuses,
                joinType = JoinType.FULL,
                additionalConstraint = { OrdersStatuses.status eq StatusesService.Statuses.id }
            )
                .selectAll()
                .map {
                    ExposedStatus(it[StatusesService.Statuses.name])
                }
                .firstOrNull() ?: return@dbQuery null
            OrdersStatuses.selectAll()
                .where { OrdersStatuses.id eq id }
                .map {
                    ExposedOrderStatus(
                        lastUpdate = it[OrdersStatuses.lastUpdate],
                        status = status
                    )
                }.singleOrNull()
        }

    suspend fun update(
        id: Int,
        orderStatus: ExposedOrderStatus,
        statusId: Int?
    ) {
        dbQuery {
            OrdersStatuses.update({ OrdersStatuses.id eq id }) {
                it[lastUpdate] = orderStatus.lastUpdate
                if (statusId != null) {
                    it[status] = statusId
                }
            }
        }
    }
}