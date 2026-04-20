package com.ecumeno.data.local.database.entities

data class Book(
    val bookNumber: Int,
    val shortName: String,
    val longName: String,
    val apocrypha: Int,
    val orthodox: Int
)