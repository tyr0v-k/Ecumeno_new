package com.uvpv521.calendar.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "books",
    indices = [Index(value = ["bookNumber", "longName"])]
)
data class Book(
    @PrimaryKey
    val bookNumber: Int,
    val shortName: String,
    val longName: String,
    val apocrypha: Int,
    val orthodox: Int
)