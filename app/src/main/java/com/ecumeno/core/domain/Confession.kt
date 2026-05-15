package com.ecumeno.core.domain

enum class Confession {
    ort,
    cat,
    lut;

    companion object {
        fun fromPreferences(value: String?): Confession =
            when (value) {
                "cat" -> cat
                "lut" -> lut
                else -> ort
            }

        fun toPreferences(confession: Confession): String = confession.name
    }
}