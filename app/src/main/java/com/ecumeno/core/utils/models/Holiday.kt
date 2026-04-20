package com.ecumeno.core.utils.models

import android.os.Parcelable
//import com.ecumeno.data.database.Converters
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class Holiday(
    val name: String,
    val priority: Int, // 1-6 по уставу
) : Parcelable