package com.diego.options.fetcher

import com.diego.options.data.OptionChain


interface OptionsFetcher {
    fun fetch(symbol: String): List<OptionChain>
}
