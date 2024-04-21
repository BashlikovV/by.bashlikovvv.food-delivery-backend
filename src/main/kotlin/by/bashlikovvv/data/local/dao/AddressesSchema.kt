package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.AddressesTable
import by.bashlikovvv.util.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedAddress(
    val house: Int,
    val floor: Int,
    val apartment: Int,
    val postcode: String,
    val city: ExposedCity,
    val street: ExposedStreet,
)

class AddressesService(database: Database) {
    object Addresses : Table(AddressesTable.TABLE_NAME) {
        val id = integer(AddressesTable.COLUMN_ID).autoIncrement()
        val house = integer(AddressesTable.COLUMN_HOUSE)
        val floor = integer(AddressesTable.COLUMN_FLOOR)
        val apartment = integer(AddressesTable.COLUMN_APARTMENT)
        val postcode = varchar(AddressesTable.COLUMN_POSTCODE, 6)
        val city = integer(AddressesTable.COLUMN_ADDRESS_CITIES_FK).references(CitiesService.Cities.id)
        val street = integer(AddressesTable.COLUMN_ADDRESS_STREETS_FK).references(StreetsService.Streets.id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Addresses)
        }
    }

    suspend fun create(
        address: ExposedAddress,
        cityId: Int,
        streetId: Int
    ): Int =
        dbQuery {
            Addresses.insert {
                it[house] = address.house
                it[floor] = address.floor
                it[apartment] = address.apartment
                it[postcode] = address.postcode
                it[city] = cityId
                it[street] = streetId
            }[Addresses.id]
        }

    suspend fun read(id: Int): ExposedAddress? =
        dbQuery {
            val cityJoin = Join(
                table = Addresses,
                otherTable = CitiesService.Cities,
                joinType = JoinType.FULL,
                additionalConstraint = { Addresses.city eq CitiesService.Cities.id }
            )
            val countryJoin = Join(
                table = CitiesService.Cities,
                otherTable = CountriesService.Countries,
                joinType = JoinType.FULL,
                additionalConstraint = { CitiesService.Cities.country eq CountriesService.Countries.id }
            )
            val streetJoin = Join(
                table = Addresses,
                otherTable = StreetsService.Streets,
                joinType = JoinType.FULL,
                additionalConstraint = { Addresses.street eq StreetsService.Streets.id }
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
            Addresses.selectAll()
                .where { Addresses.id eq id }
                .map { addressDto ->
                    ExposedAddress(
                        house = addressDto[Addresses.house],
                        floor = addressDto[Addresses.floor],
                        postcode = addressDto[Addresses.postcode],
                        apartment = addressDto[Addresses.apartment],
                        city = city,
                        street = street,
                    )
                }.singleOrNull()
        }

    suspend fun update(
        id: Int,
        address: ExposedAddress,
        cityId: Int?,
        streetId: Int?,
    ) {
        dbQuery {
            Addresses.update({ Addresses.id eq id }) {
                it[house] = address.house
                it[floor] = address.floor
                it[postcode] = address.postcode
                it[apartment] = address.apartment
                if (cityId != null) {
                    it[city] = cityId
                }
                if (streetId != null) {
                    it[street] = streetId
                }
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Addresses.deleteWhere { Addresses.id eq id }
        }
    }
}