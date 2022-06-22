package com.diego.options.data

import java.math.BigDecimal

data class Option(
    val expiration: Date,
    val type: OptionType,
    val strike: BigDecimal,
    val ask: BigDecimal,
    val bid: BigDecimal,
    val last: BigDecimal,
    val openInterest: Int,
    val volume: Int,
)

enum class OptionType {
    PUT, CALL
}
