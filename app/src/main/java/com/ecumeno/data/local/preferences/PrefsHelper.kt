package com.ecumeno.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PrefsHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean("onboarding_completed", false)
        set(value) = prefs.edit { putBoolean("onboarding_completed", value) }
    var isNotificationEnabled: Boolean
        get() = prefs.getBoolean("notif_enabled", false)
        set(value) = prefs.edit { putBoolean("notif_enabled", value) }
    var isRuleEnabled: Boolean
        get() = prefs.getBoolean("rule_enabled", true)
        set(value) = prefs.edit { putBoolean("rule_enabled", value) }
    var notificationHour: Int
        get() = prefs.getInt("notif_hour", 9)
        set(value) = prefs.edit { putInt("notif_hour", value) }
    var notificationMinute: Int
        get() = prefs.getInt("notif_minute", 0)
        set(value) = prefs.edit { putInt("notif_minute", value) }
    var fontSize: Float
        get() = prefs.getFloat("font_size", 18f)
        set(value) = prefs.edit { putFloat("font_size", value) }
    var lastBook: Int
        get() = prefs.getInt("last_book", -1)
        set(value) = prefs.edit { putInt("last_book", value) }
    var lastChapter: Int
        get() = prefs.getInt("last_chapter", -1)
        set(value) = prefs.edit { putInt("last_chapter", value) }
    var confession: String
        get() = prefs.getString("confession", "ort").toString()
        set(value) = prefs.edit { putString("confession", value) }
    var nightMode: Int
        get() = prefs.getInt("night_mode", -1)
        set(value) = prefs.edit { putInt("night_mode", value) }
}