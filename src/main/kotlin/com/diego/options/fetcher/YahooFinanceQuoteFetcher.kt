package com.diego.options.fetcher

import com.diego.options.data.Quote
import com.diego.options.data.today
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.math.BigDecimal
import java.net.URL
import java.text.MessageFormat

class YahooFinanceQuoteFetcher : QuoteFetcher {

    companion object Constants {
        const val URL_TEMPLATE = "https://query1.finance.yahoo.com/v7/finance/quote?symbols={0}"
    }

    override fun fetch(symbol: String): Quote {
        val data = getURL(symbol).readText()
        val jsonNode: JsonNode = parse(data)
        return convertToQuote(jsonNode)
    }

    private fun getURL(symbol: String): URL {
        val formatted = MessageFormat.format(URL_TEMPLATE, symbol)
        return URL(formatted)
    }

    private fun parse(json: String): JsonNode {
        val mapper = jacksonObjectMapper()
        return mapper.readTree(json)
    }

    private fun convertToQuote(jsonNode: JsonNode): Quote {
        val priceStr =  jsonNode
            .get("quoteResponse")
            .get("result")
            .get(0)
            .get("regularMarketPrice")
            .asText()
        val price = BigDecimal(priceStr)
        return Quote(price = price, date = today())
    }
}
