package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.UserAddressTable
import by.bashlikovvv.util.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedUserAddress(val address: ExposedAddress)

class UserAddressService(database: Database) {
    object UserAddresses : Table(UserAddressTable.TABLE_NAME) {
        val id = integer(UserAddressTable.COLUMN_ID).autoIncrement()
        val address = integer(UserAddressTable.COLUMN_USER_ADDRESS_ADDRESS_FK).references(AddressesService.Addresses.id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(UserAddresses)
        }
    }

    suspend fun create(addressId: Int): Int =
        dbQuery {
            UserAddresses.insert {
                it[address] = addressId
            }[UserAddresses.id]
        }


    suspend fun read(id: Int): ExposedUserAddress? =
        dbQuery {
            val addressJoin = Join(
                table = UserAddresses,
                otherTable = AddressesService.Addresses,
                joinType = JoinType.FULL,
                additionalConstraint = { UserAddresses.id eq AddressesService.Addresses.id }
            )
            val cityJoin = Join(
                table = AddressesService.Addresses,
                otherTable = CitiesService.Cities,
                joinType = JoinType.FULL,
                additionalConstraint = { AddressesService.Addresses.city eq CitiesService.Cities.id }
            )
            val countryJoin = Join(
                table = CitiesService.Cities,
                otherTable = CountriesService.Countries,
                joinType = JoinType.FULL,
                additionalConstraint = { CitiesService.Cities.country eq CountriesService.Countries.id }
            )
            val streetJoin = Join(
                table = AddressesService.Addresses,
                otherTable = StreetsService.Streets,
                joinType = JoinType.FULL,
                additionalConstraint = { AddressesService.Addresses.street eq StreetsService.Streets.id }
            )
            val city = cityJoin.selectAll()
                .map {  cityDto ->
                    ExposedCity(
                        name = cityDto[CitiesService.Cities.name],
                        timeZone = cityDto[CitiesService.Cities.timeZone],
                        country = countryJoin.selectAll()
                            .map {
                                ExposedCountry(it[CountriesService.Countries.name])
                            }.firstOrNull()
                    )
                }.firstOrNull() ?: return@dbQuery null
            val street = streetJoin.selectAll()
                .map {
                    ExposedStreet(it[StreetsService.Streets.name])
                }.firstOrNull() ?: return@dbQuery null
            val address = addressJoin.selectAll()
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
            UserAddresses.selectAll()
                .where { UserAddresses.id eq id }
                .map {
                    ExposedUserAddress(address = address)
                }
                .singleOrNull()
        }
    suspend fun update(id: Int, addressId: Int) {
        dbQuery {
            UserAddresses.update({ UserAddresses.id eq id }) {
                it[UserAddresses.address] = addressId
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            UserAddresses.deleteWhere { UserAddresses.id eq id }
        }
    }
}