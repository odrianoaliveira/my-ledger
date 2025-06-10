package com.adriano.ledger.account

import com.adriano.ledger.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateAccountRequest(
    val name: String,
    @Serializable(UUIDSerializer::class) val ownerId: UUID
)

@Serializable
data class Account(
    @Serializable(UUIDSerializer::class) val id: UUID,
    val name: String,
    @Serializable(UUIDSerializer::class) val ownerId: UUID
)