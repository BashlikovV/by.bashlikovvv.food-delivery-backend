package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.OrdersTable
import by.bashlikovvv.util.dbQuery
import org.jetbrains.exposed.sql.*

data class ExposedOrder(
    val address: ExposedUserAddress,
    val date: Long,
    val rate: ExposedOrderRate,
    val status: ExposedOrderStatus
)

class OrdersService(database: Database) {
    object Orders : Table(OrdersTable.TABLE_NAME) {
        val id = integer(OrdersTable.COLUMN_ID).autoIncrement()
        val address = integer(OrdersTable.COLUMN_ORDER_ADDRESS_FK).references(UserAddressService.UserAddresses.id)
        val date = long(OrdersTable.COLUMN_DATE)
        val rate = integer(OrdersTable.COLUMN_ORDER_RATE_FK).references(OrderRatesService.OrdersRates.id)
        val status = integer(OrdersTable.COLUM_ORDER_STATUS_FK).references(OrderStatusesService.OrdersStatuses.id)
    }

    suspend fun create(
        order: ExposedOrder,
        addressId: Int,
        rateId: Int,
        statusId: Int
    ): Int =
        dbQuery {
            Orders.insert {
                it[address] = addressId
                it[date] = order.date
                it[rate] = rateId
                it[status] = statusId
            }[Orders.id]
        }

    suspend fun read(id: Int): ExposedOrder? =
        dbQuery {
            val country = Join(
                table = CitiesService.Cities,
                otherTable = CountriesService.Countries,
                joinType = JoinType.FULL,
                additionalConstraint = { CitiesService.Cities.country eq CountriesService.Countries.id }
            )
                .selectAll()
                .map {
                    ExposedCountry(it[CountriesService.Countries.name])
                }.firstOrNull()
            val street = Join(
                table = AddressesService.Addresses,
                otherTable = StreetsService.Streets,
                joinType = JoinType.FULL,
                additionalConstraint = { AddressesService.Addresses.street eq StreetsService.Streets.id }
            )
                .selectAll()
                .map {
                    ExposedStreet(it[StreetsService.Streets.name])
                }.firstOrNull() ?: return@dbQuery null
            val city = Join(
                table = AddressesService.Addresses,
                otherTable = CitiesService.Cities,
                joinType = JoinType.FULL,
                additionalConstraint = { AddressesService.Addresses.city eq CitiesService.Cities.id }
            )
                .selectAll()
                .map { cityDto ->
                    ExposedCity(
                        name = cityDto[CitiesService.Cities.name],
                        timeZone = cityDto[CitiesService.Cities.timeZone],
                        country = country
                    )
                }.firstOrNull() ?: return@dbQuery null
            val address = Join(
                table = UserAddressService.UserAddresses,
                otherTable = AddressesService.Addresses,
                joinType = JoinType.FULL,
                additionalConstraint = { UserAddressService.UserAddresses.id eq AddressesService.Addresses.id }
            )
                .selectAll()
                .map { addressDto ->
                    ExposedAddress(
                        house = addressDto[AddressesService.Addresses.house],
                        floor = addressDto[AddressesService.Addresses.floor],
                        postcode = addressDto[AddressesService.Addresses.postcode],
                        apartment = addressDto[AddressesService.Addresses.apartment],
                        city = city,
                        street = street,
                    )
                }.firstOrNull() ?: return@dbQuery null
            val userAddress = Join(
                table = Orders,
                otherTable = UserAddressService.UserAddresses,
                joinType = JoinType.FULL,
                additionalConstraint = { Orders.address eq UserAddressService.UserAddresses.id }
            )
                .selectAll()
                .map {
                    ExposedUserAddress(address)
                }.firstOrNull() ?: return@dbQuery null
            val rate = Join(
                table = Orders,
                otherTable = OrderRatesService.OrdersRates,
                joinType = JoinType.FULL,
                additionalConstraint = { Orders.rate eq OrderRatesService.OrdersRates.id }
            )
                .selectAll()
                .map {
                    ExposedOrderRate(
                        status = it[OrderRatesService.OrdersRates.status],
                        comment = it[OrderRatesService.OrdersRates.comment]
                    )
                }
                .firstOrNull() ?: return@dbQuery null
            val status = Join(
                table = OrderStatusesService.OrdersStatuses,
                otherTable = StatusesService.Statuses,
                joinType = JoinType.FULL,
                additionalConstraint = { OrderStatusesService.OrdersStatuses.status eq StatusesService.Statuses.id }
            )
                .selectAll()
                .map {
                    ExposedStatus(it[StatusesService.Statuses.name])
                }
                .firstOrNull() ?: return@dbQuery null
            val orderStatus = Join(
                table = Orders,
                otherTable = OrderStatusesService.OrdersStatuses,
                joinType = JoinType.FULL,
                additionalConstraint = { Orders.status eq OrderStatusesService.OrdersStatuses.id }
            )
                .selectAll()
                .map {
                    ExposedOrderStatus(
                        lastUpdate = it[OrderStatusesService.OrdersStatuses.lastUpdate],
                        status = status
                    )
                }
                .firstOrNull() ?: return@dbQuery null
            Orders.selectAll()
                .where { Orders.id eq id }
                .map {
                    ExposedOrder(
                        address = userAddress,
                        date = it[Orders.date],
                        rate = rate,
                        status = orderStatus
                    )
                }.singleOrNull()
        }

    suspend fun update(
        id: Int,
        order: ExposedOrder,
        addressId: Int?,
        rateId: Int?,
        statusId: Int?,
    ) {
        dbQuery {
            Orders.update({ Orders.id eq id }) {
                it[date] = order.date
                if (addressId != null) {
                    it[address] = addressId
                }
                if (rateId != null) {
                    it[rate] = rateId
                }
                if (statusId != null) {
                    it[status] = statusId
                }
            }
        }
    }
}