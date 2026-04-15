package com.uvpv521.calendar.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "prayers",
    indices = [Index(value = ["categoryNumber", "prayerPosition", "prayerName"])]
)
data class Prayer(
    @PrimaryKey
    val categoryNumber: Int,
    val prayerPosition: Int,
    val prayerName: String,
    val text: String
)