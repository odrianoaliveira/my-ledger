package com.adriano

import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    val id: String,
    val accountId: String,
    val amount: Long,
    val direction: Direction
)

@Serializable
enum class Direction(val value: String) {
    DEBIT("debit"),
    CREDIT("credit");

    override fun toString(): String = value
}
