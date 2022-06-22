package com.diego.options

import com.diego.options.data.Date
import com.diego.options.data.OptionChain
import com.diego.options.data.OptionType.PUT
import com.diego.options.data.Stock
import com.diego.options.data.VerticalSpread

class VerticalSpreadFinder {

    fun findAll(stock: Stock): List<VerticalSpread> {
        return stock.options
            .map { findSpreads(it, stock) }
            .flatten()
    }

    private fun findSpreads(optionChain: OptionChain, stock: Stock): List<VerticalSpread> {
        val puts = optionChain.options.filter { it.type == PUT }
        val spreads: MutableList<VerticalSpread> = mutableListOf()
        for (buyPut in puts) {
            for(sellPut in puts) {
                if(sellPut.strike >= stock.quote.price) {
                    continue
                }
                if(buyPut.strike >= sellPut.strike) {
                    continue
                }
                if(buyPut.last >= sellPut.last) {
                    continue
                }
                val spread = VerticalSpread(
                    symbol = stock.symbol,
                    quote = stock.quote,
                    buyOption = buyPut,
                    sellOption = sellPut,
                    expiration = optionChain.expiration
                )
                spreads.add(spread)
            }
        }
        return spreads
    }
}
