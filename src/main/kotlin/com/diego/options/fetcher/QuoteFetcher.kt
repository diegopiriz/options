package com.diego.options.fetcher

import com.diego.options.data.Quote

interface QuoteFetcher {
    fun fetch(symbol: String): Quote
}
