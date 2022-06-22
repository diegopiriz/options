package com.diego.options.data

data class OptionChain(
    val expiration: Date,
    val options: List<Option>
)
