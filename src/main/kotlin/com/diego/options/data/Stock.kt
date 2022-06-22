package com.diego.options.data

data class Stock(
    val symbol: String,
    val quote: Quote,
    val options: List<OptionChain>
)
