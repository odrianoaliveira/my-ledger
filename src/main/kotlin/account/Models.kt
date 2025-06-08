package com.adriano.account

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequest(
    val name: String,
    val ownerId: String
)

@Serializable
data class Account(
    val id: String,
    val name: String,
    val ownerId: String
)