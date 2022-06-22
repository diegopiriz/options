package com.diego.options

import com.diego.options.fetcher.*
import java.math.BigDecimal


private const val MIN_RETURN_ON_RISK_THRESHOLD = 0.05
private const val MIN_FALL_TO_LOOSE = 0.02
private const val MIN_DAYS_TO_EXPIRATION_THRESHOLD = 0
private const val MAX_DAYS_TO_EXPIRATION_THRESHOLD = 90
private const val MAX_RESULTS = 10
private val STOCKS = listOf(
	"TSLA", "AAPL", "AAL", "AMZN", "NIO", "MSFT", "AMD", "BA", "NFLX", "DAL", "KO", "GOOGL", "FB", "DIS",
	"SPCE", "NCLH", "BABA", "NVDA", "ZM", "CCL", "BYND", "INTC", "IVR", "F", "XOM", "T", "SHOP",
	"UBER", "MA", "V", "RCL", "GE", "PFE", "ATVI", "MCD", "GRPN", "ABEV", "GILD", "BAC", "AMRX", "GPRO", "SONY",
	"BKNG", "MRO", "MMM", "ADBE", "SBUX", "SQ", "JMIA"
)

//private val STOCKS = listOf("GOOGL")

fun main(args: Array<String>) {
	val quoteFetcher = YahooFinanceQuoteFetcher()
	//val optionsFetcher = OptionsProfitCalculatorFetcher()
	val optionsFetcher = YahooFinanceOptionsFetcher()
	val stockFetcher = StockFetcher(quoteFetcher, optionsFetcher)
	val verticalSpreadFinder = VerticalSpreadFinder()


	STOCKS.asSequence()
		.map { symbol ->
			try {
				val stock = stockFetcher.fetch(symbol)
				val opportunities = verticalSpreadFinder.findAll(stock)
				println("$symbol -> ${opportunities.size}")
				opportunities
			} catch (ex: Exception) {
				null
			}
		}
		.filterNotNull()
		.flatten()
		.filter { it.safetyMargin > BigDecimal(MIN_FALL_TO_LOOSE) }
		//.filter { it.returnOnRisk > BigDecimal(MIN_RETURN_ON_RISK_THRESHOLD)}
		.filter { it.returnOnRisk < BigDecimal("1.00") }
		.filter { it.daysToExpiration > MIN_DAYS_TO_EXPIRATION_THRESHOLD}
		.filter { it.daysToExpiration < MAX_DAYS_TO_EXPIRATION_THRESHOLD}
		.sortedWith(compareBy( {it.returnOnRisk}, {it.safetyMargin}, {it.daysToExpiration}))
		.toList()
		.reversed()
		.take(MAX_RESULTS)
		.forEach { println(it.shortStr()) }


	//val mapper = jacksonObjectMapper()
	//println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(opportunities))

	//println("Hello, world!")
}




