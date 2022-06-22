package com.diego.options.fetcher

import com.diego.options.data.Stock

class StockFetcher(
    private val quoteFetcher: QuoteFetcher,
    private val optionsFetcher: OptionsFetcher
) {
    fun fetch(symbol: String): Stock {
        val quote = quoteFetcher.fetch(symbol)
        val options = optionsFetcher.fetch(symbol)
        return Stock(symbol, quote, options)
    }
}
