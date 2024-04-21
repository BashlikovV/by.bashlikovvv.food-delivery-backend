package by.bashlikovvv.data.local.dao

import by.bashlikovvv.data.local.contract.PsqlContract.UsersTable
import by.bashlikovvv.util.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedUser(
    val email: String,
    val salt: String,
    val hash: String,
    val phone: String,
    val firstname: String,
    val lastname: String,
    val type: ExposedUserType,
    val birthdate: Long,
    val address: ExposedUserAddress?,
    val paymentCart: ExposedPaymentCart?
)

class UsersService(database: Database) {
    object Users : Table(UsersTable.TABLE_NAME) {
        val id = integer(UsersTable.COLUMN_ID).autoIncrement()
        val email = varchar(UsersTable.COLUMN_EMAIL, 256)
        val salt = varchar(UsersTable.COLUMN_SALT, 256)
        val hash = varchar(UsersTable.COLUM_HASH, 256)
        val phone = varchar(UsersTable.COLUMN_PHONE, 256)
        val firstname = varchar(UsersTable.COLUMN_FIRSTNAME, 256)
        val lastname = varchar(UsersTable.COLUMN_LASTNAME, 256)
        val type = integer(UsersTable.COLUMN_USER_TYPE_FK).references(UserTypesService.UserTypes.id)
        val birthdate = long(UsersTable.COLUMN_BIRTHDATE)
        val address = integer(UsersTable.COLUMN_USER_USER_ADDRESS_FK).references(UserAddressService.UserAddresses.id)
        val paymentCart = integer(UsersTable.COLUMN_USER_PAYMENT_CART_FK).references(PaymentCartsService.PaymentCarts.id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun create(
        user: ExposedUser,
        typeId: Int,
        addressId: Int,
        paymentCartId: Int
    ): Int =
        dbQuery {
            Users.insert {
                it[email] = user.email
                it[salt] = user.salt
                it[hash] = user.hash
                it[phone] = user.phone
                it[firstname] = user.firstname
                it[lastname] = user.lastname
                it[type] = typeId
                it[birthdate] = user.birthdate
                it[address] = addressId
                it[paymentCart] = paymentCartId
            }[Users.id]
        }

    suspend fun read(id: Int): ExposedUser? =
        dbQuery {
            val type = Join(
                table = Users,
                otherTable = UserTypesService.UserTypes,
                joinType = JoinType.FULL,
                additionalConstraint = { Users.type eq UserTypesService.UserTypes.id }
            )
                .selectAll()
                .map {
                    ExposedUserType(it[UserTypesService.UserTypes.name])
                }
                .firstOrNull() ?: return@dbQuery null
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
                table = Users,
                otherTable = UserAddressService.UserAddresses,
                joinType = JoinType.FULL,
                additionalConstraint = { Users.address eq UserAddressService.UserAddresses.id }
            )
                .selectAll()
                .map {
                    ExposedUserAddress(
                        address
                    )
                }
                .firstOrNull()
            val paymentCart = Join(
                table = Users,
                otherTable = PaymentCartsService.PaymentCarts,
                joinType = JoinType.FULL,
                additionalConstraint = { Users.paymentCart eq PaymentCartsService.PaymentCarts.id }
            )
                .selectAll()
                .map {
                    ExposedPaymentCart(
                        number = it[PaymentCartsService.PaymentCarts.number],
                        system = it[PaymentCartsService.PaymentCarts.system],
                        default = it[PaymentCartsService.PaymentCarts.default],
                        email = it[PaymentCartsService.PaymentCarts.email]
                    )
                }.firstOrNull()
            Users.selectAll()
                .where { Users.id eq id }
                .map {
                    ExposedUser(
                        email = it[Users.email],
                        salt = it[Users.salt],
                        hash = it[Users.hash],
                        phone = it[Users.phone],
                        firstname = it[Users.firstname],
                        lastname = it[Users.lastname],
                        type = type,
                        birthdate = it[Users.birthdate],
                        address = userAddress,
                        paymentCart = paymentCart
                    )
                }
                .firstOrNull()
        }

    suspend fun update(
        id: Int,
        user: ExposedUser,
        typeId: Int?,
        addressId: Int?,
        paymentCartId: Int?
    ) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[email] = user.email
                it[salt] = user.salt
                it[hash] = user.hash
                it[phone] = user.phone
                it[firstname] = user.firstname
                it[lastname] = user.lastname
                it[birthdate] = user.birthdate
                if (typeId != null) {
                    it[type] = typeId
                }
                if (addressId != null) {
                    it[address] = addressId
                }
                if (paymentCartId != null) {
                    it[paymentCart] = paymentCartId
                }
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id eq id }
        }
    }
}