package by.bashlikovvv.api.dto.request

import by.bashlikovvv.data.local.dao.ExposedUser
import kotlinx.serialization.Serializable

@Serializable
data class CheckUserResponseDto(
    val errorMessage: String = "",
    val user: ExposedUser? = null
)