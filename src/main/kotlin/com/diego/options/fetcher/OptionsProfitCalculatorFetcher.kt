package com.diego.options.fetcher

import com.diego.options.data.Date
import com.diego.options.data.Option
import com.diego.options.data.OptionChain
import com.diego.options.data.OptionType
import com.diego.options.data.OptionType.CALL
import com.diego.options.data.OptionType.PUT
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.BufferedReader
import java.math.BigDecimal
import java.net.URL
import java.text.MessageFormat
import kotlin.random.Random


class OptionsProfitCalculatorFetcher : OptionsFetcher {

    companion object Constants {
        const val URL_TEMPLATE = "https://www.optionsprofitcalculator.com/ajax/getOptions?stock={0}&reqId={1}"
    }

    override fun fetch(symbol: String): List<OptionChain> {
        val data = getData(symbol)
        val jsonNode: JsonNode = parse(data)
        return convertToOptionChain(jsonNode)
    }


    private fun getData(symbol: String): String {
        val reqId: Int = Random.nextInt(1, 100)
        val formatted = MessageFormat.format(URL_TEMPLATE, symbol, reqId)
        val url = URL(formatted)
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

    private fun convertToOptionChain(jsonNode: JsonNode): List<OptionChain> {
        return jsonNode.get("options").fields()
            .asSequence()
            .map { entry ->
                val expiration = parseDate(entry.key)
                val calls = parseOptions(entry.value.get("c"), CALL, expiration)
                val puts = parseOptions(entry.value.get("p"), PUT, expiration)
                OptionChain(expiration, calls + puts)
            }
            .toList()
    }

    private fun parseOptions(jsonNode: JsonNode, type: OptionType, expiration: Date): List<Option> {
        return jsonNode.fields()
            .asSequence()
            .map { entry ->
                val strike = BigDecimal(entry.key)
                val a = BigDecimal(entry.value.get("a").asText())
                val b = BigDecimal(entry.value.get("b").asText())
                val l = BigDecimal(entry.value.get("l").asText())
                val oi = entry.value.get("oi").asInt()
                val v = entry.value.get("v").asInt()
                Option(
                    expiration = expiration,
                    type = type,
                    strike = strike,
                    ask = a,
                    bid = b,
                    last = l,
                    openInterest = oi,
                    volume = v
                )
            }
            .toList()
    }

    private fun parseDate(str: String): Date {
        val parts = str.split("-")
        return Date(parts[0].toInt(), parts[1].toInt()-1, parts[2].toInt())
    }
}
