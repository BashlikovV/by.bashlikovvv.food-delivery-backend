package by.bashlikovvv.api.dto.request

import by.bashlikovvv.data.local.dao.ExposedCart

data class CreateCartDto(
    val cart: ExposedCart,
    val amountId: Int,
    val productId: Int,
    val userId: Int
)