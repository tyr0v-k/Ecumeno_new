package com.uvpv521.calendar.data.local.preferences

import android.content.Context
import android.content.SharedPreferences

class PrefsHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    var isNotificationEnabled: Boolean
        get() = prefs.getBoolean("notif_enabled", false)
        set(value) = prefs.edit().putBoolean("notif_enabled", value).apply()
    var isRuleEnabled: Boolean
        get() = prefs.getBoolean("rule_enabled", true)
        set(value) = prefs.edit().putBoolean("rule_enabled", value).apply()
    var notificationHour: Int
        get() = prefs.getInt("notif_hour", 9)
        set(value) = prefs.edit().putInt("notif_hour", value).apply()

    var notificationMinute: Int
        get() = prefs.getInt("notif_minute", 0)
        set(value) = prefs.edit().putInt("notif_minute", value).apply()

    var fontSize: Float
        get() = prefs.getFloat("font_size", 18f)
        set(value) = prefs.edit().putFloat("font_size", value).apply()

    var lastBook: Int
        get() = prefs.getInt("last_book", -1)
        set(value) = prefs.edit().putInt("last_book", value).apply()

    var lastChapter: Int
        get() = prefs.getInt("last_chapter", -1)
        set(value) = prefs.edit().putInt("last_chapter", value).apply()

    var confession: String
        get() = prefs.getString("confession", "ort").toString()
        set(value) = prefs.edit().putString("confession", value).apply()
}