package com.diego.options.fetcher

import com.diego.options.data.Date
import com.diego.options.data.Option
import com.diego.options.data.OptionChain
import com.diego.options.data.OptionType
import com.diego.options.data.OptionType.CALL
import com.diego.options.data.OptionType.PUT
import com.diego.options.data.fromSeconds
import com.diego.options.data.today
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.BufferedReader
import java.math.BigDecimal
import java.net.URL
import java.text.MessageFormat


class YahooFinanceOptionsFetcher : OptionsFetcher {

    companion object Constants {
        const val EXPIRATIONS_URL_TEMPLATE = "https://query1.finance.yahoo.com/v7/finance/options/{0}"
        const val OPTIONS_URL_TEMPLATE = "https://query1.finance.yahoo.com/v7/finance/options/{0}?date={1}"
    }

    override fun fetch(symbol: String): List<OptionChain> {
        return getExpirationsTimestamps(symbol)
            .map {
                val url = MessageFormat.format(OPTIONS_URL_TEMPLATE, symbol, it.toString())
                val jsonNode = get(url)
                parseOptionChain(jsonNode)
            }
            .toList()
    }

    private fun getExpirationsTimestamps(symbol: String): List<Long> {
        val url = MessageFormat.format(EXPIRATIONS_URL_TEMPLATE, symbol)
        val jsonNode = get(url)
        return jsonNode
            .get("optionChain")
            .get("result")
            .get(0)
            .get("expirationDates")
            .elements()
            .asSequence()
            .map { it.asLong() }
            .toList()
    }

    private fun get(url: String): JsonNode {
        return parse(getData(url))
    }


    private fun getData(uri: String): String {
        val url = URL(uri)
        val hc = url.openConnection()
        hc.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"
        )
        return BufferedReader(hc.getInputStream().reader())
            .readText()
    }

    private fun parse(json: String): JsonNode {
        val mapper = jacksonObjectMapper()
        return mapper.readTree(json)
    }

    private fun parseOptionChain(jsonNode: JsonNode): OptionChain {
        val node = jsonNode.get("optionChain")
            .get("result")
            .get(0)
            .get("options")
            .get(0)
        val expiration = fromSeconds(node.get("expirationDate").asLong())
        val calls = parseOptions(node.get("calls"), CALL, expiration)
        val puts = parseOptions(node.get("puts"), PUT, expiration)
        return OptionChain(expiration, calls + puts)
    }

    private fun parseOptions(jsonNode: JsonNode, type: OptionType, expiration: Date): List<Option> {
        return jsonNode
            .map {
                val strike = BigDecimal(it.get("strike").asText())
                val ask = BigDecimal(it.get("ask").asText())
                val bid = BigDecimal(it.get("bid").asText())
                val last = BigDecimal(it.get("lastPrice").asText())
                val openInterest = it.get("openInterest")?.asInt() ?: 0
                val volume = it.get("volume")?.asInt() ?: 0
                val lastTraded = fromSeconds(it.get("lastTradeDate").asLong())
                if(lastTraded.daysUntil(today()) > 1) {
                    null
                } else {
                    Option(
                        expiration = expiration,
                        type = type,
                        strike = strike,
                        ask = ask,
                        bid = bid,
                        last = last,
                        openInterest = openInterest,
                        volume = volume
                    )
                }
            }
            .filterNotNull()
            .toList()
    }
}
