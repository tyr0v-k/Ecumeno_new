package com.ecumeno.ui.rosary

import com.ecumeno.core.utils.models.enums.MysteryType
import com.ecumeno.core.utils.models.enums.PrayerType

data class RosaryUiState(
    val prayerType: PrayerType = PrayerType.OUR_FATHER,
    val currentDecade: Int = 0,
    val currentBead: Int = 0,
    val displayBead: Int = 0,
    val mysteryType: MysteryType = MysteryType.JOYFUL,
    val mysteryVisibility: Boolean = false,
    val prayerVisibility: Boolean = true,
    val decadeVisibility: Boolean = false,
    val beadVisibility: Boolean = false,
    val hasCenterPiece: Boolean = false,
    val isDelimiter: Boolean = false
)