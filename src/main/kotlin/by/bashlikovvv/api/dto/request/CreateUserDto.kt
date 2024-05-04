package by.bashlikovvv.api.dto.request

import by.bashlikovvv.data.local.dao.ExposedPaymentCart
import by.bashlikovvv.data.local.dao.ExposedUser
import by.bashlikovvv.data.local.dao.ExposedUserAddress
import by.bashlikovvv.data.local.dao.ExposedUserType
import by.bashlikovvv.util.SecurityUtilsImpl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserDto(
    @SerialName("firstname") val firstname: String,
    @SerialName("lastname") val lastname: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("birthDate") val birthDate: Long? = null,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("address") val address: Int? = null,
    @SerialName("paymentCart") val paymentCart: Int? = null,
)

suspend fun CreateUserDto.toExposedUser(
    getAddressById: suspend (Int) -> ExposedUserAddress?,
    getPaymentCartById: suspend (Int) -> ExposedPaymentCart?
): ExposedUser {
    val securityUtils = SecurityUtilsImpl()
    val salt = securityUtils.generateSalt()
    val hash = securityUtils.passwordToHash(
        password = password.toCharArray(),
        salt = salt
    )

    return ExposedUser(
        id = 0,
        email = email,
        salt = salt.joinToString(),
        hash = hash.joinToString(),
        phone = phone,
        firstname = firstname,
        lastname = lastname,
        type = ExposedUserType(name = "user"),
        birthdate = birthDate,
        address = address?.let { getAddressById(it) },
        paymentCart = paymentCart?.let { getPaymentCartById(it) }
    )
}