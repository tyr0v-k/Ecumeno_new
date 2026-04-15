package com.uvpv521.calendar.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories",
    indices = [Index(value = ["categoryNumber", "name"])]
)
data class Category(
    @PrimaryKey
    val categoryNumber: Int,
    val name: String
)