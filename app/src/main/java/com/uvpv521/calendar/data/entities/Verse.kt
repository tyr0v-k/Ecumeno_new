package com.uvpv521.calendar.data.entities

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "verses",
    indices = [Index(value = ["bookNumber", "chapter", "verse"])]
)
data class Verse(
    val bookNumber: Int,
    val chapter: Int,
    val verse: Int,
    val text: String
)