package com.diego.options.data

import java.math.BigDecimal

data class VerticalSpread(
    val symbol: String,
    val quote: Quote,
    val buyOption: Option,
    val sellOption: Option,
    val expiration: Date,
) {

    val safetyMargin = (quote.price - sellOption.strike) / quote.price
    val entryCredit = sellOption.last - buyOption.last
    val maxRisk = sellOption.strike - buyOption.strike - entryCredit
    val returnOnRisk = if(maxRisk <= BigDecimal.ZERO) BigDecimal.ZERO else (entryCredit / maxRisk)
    val daysToExpiration = quote.date.daysUntil(sellOption.expiration)

    fun shortStr(): String {
        val header = "SYMBOL: $symbol @$${quote.price}"
        val buyStr  = "$expiration ($daysToExpiration days left) BUY ($${buyOption.strike}  @$${buyOption.last}) -> -$${buyOption.last.movePointRight(2)}"
        val sellStr = "$expiration ($daysToExpiration days left) SELL($${sellOption.strike}  @$${sellOption.last}) -> +$${sellOption.last.movePointRight(2)}"
        val riskStr = "Entry Credit: +$${entryCredit.movePointRight(2)}, Max Risk: $${maxRisk.movePointRight(2)} -> ${returnOnRisk.movePointRight(2)}%"
        val daysToExpirationStr = "Days to expiration: $daysToExpiration"
        val safety = "Safety margin: ${safetyMargin.movePointRight(2)}%"
        return "$header\n" +
                "$buyStr\n" +
                "$sellStr\n" +
                "$riskStr\n" +
                "$safety\n" +
                "$daysToExpirationStr\n" +
                "--------------------------------------------"
    }
}
