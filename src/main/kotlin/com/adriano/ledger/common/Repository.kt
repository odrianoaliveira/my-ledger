package com.adriano.ledger.common

import java.util.*

interface Repository<T> {

    fun save(value: T): T
    fun findById(id: UUID): T?
    fun findAll(): List<T>
}