package com.ecumeno.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import com.ecumeno.data.local.preferences.Confession

data class SettingsUiState(
    val isNotificationEnabled: Boolean = false,
    val isRuleEnabled: Boolean = false,
    val notificationTime: String = "09:00",
    val notificationHour: Int = 9,
    val notificationMinute: Int = 0,
    val nightMode: Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
    val selectedThemePosition: Int = 0,
    val confession: Confession = Confession.ort,
    val selectedConfessionPosition: Int = 0,
    val selectedLanguagePosition: Int = 0,
    val currentLanguageCode: String = "en"
)