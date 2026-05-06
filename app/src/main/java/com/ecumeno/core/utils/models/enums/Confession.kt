package com.ecumeno.core.utils.models.enums

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