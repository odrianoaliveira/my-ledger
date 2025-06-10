package com.adriano.ledger.transaction

import com.adriano.ledger.common.UUIDSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateTransactionRequest(
    val description: String,
    val timestamp: Instant,
    val entries: List<Entry>
)

@Serializable
data class Entry(
    @Serializable(with = UUIDSerializer::class) val accountId: UUID,
    val amount: Long,
    val direction: Direction
)

@Serializable
enum class Direction {
    @SerialName("debit")
    DEBIT,

    @SerialName("credit")
    CREDIT
}

@Serializable
data class Transaction(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val description: String,
    val entries: List<Entry>,
    val timestamp: Instant,
    val createdAt: Instant
)


