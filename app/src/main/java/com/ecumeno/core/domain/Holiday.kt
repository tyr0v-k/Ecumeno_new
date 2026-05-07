package com.ecumeno.core.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Holiday(
    val name: String,
    val priority: Int,
) : Parcelable