package com.ecumeno.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PrefsHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _isNotificationEnabled = MutableStateFlow(prefs.getBoolean("notif_enabled", false))
    private val _isRuleEnabled = MutableStateFlow(prefs.getBoolean("rule_enabled", true))
    private val _notificationHour = MutableStateFlow(prefs.getInt("notif_hour", 9))
    private val _notificationMinute = MutableStateFlow(prefs.getInt("notif_minute", 0))
    private val _fontSize = MutableStateFlow(prefs.getFloat("font_size", 18f))
    private val _lastBook = MutableStateFlow(prefs.getInt("last_book", -1))
    private val _lastChapter = MutableStateFlow(prefs.getInt("last_chapter", -1))
    private val _confession = MutableStateFlow(prefs.getString("confession", "").toString())
    private val _nightMode = MutableStateFlow(prefs.getInt("night_mode", -1))

    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()
    val isRuleEnabled: StateFlow<Boolean> = _isRuleEnabled.asStateFlow()
    val notificationHour: StateFlow<Int> = _notificationHour.asStateFlow()
    val notificationMinute: StateFlow<Int> = _notificationMinute.asStateFlow()
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()
    val lastBook: StateFlow<Int> = _lastBook.asStateFlow()
    val lastChapter: StateFlow<Int> = _lastChapter.asStateFlow()
    val confession: StateFlow<String> = _confession.asStateFlow()
    val nightMode: StateFlow<Int> = _nightMode.asStateFlow()

    fun setNotificationEnabled(value: Boolean) {
        prefs.edit { putBoolean("notif_enabled", value) }
        _isNotificationEnabled.value = value
    }

    fun setRuleEnabled(value: Boolean) {
        prefs.edit { putBoolean("rule_enabled", value) }
        _isRuleEnabled.value = value
    }

    fun setNotificationHour(value: Int) {
        prefs.edit { putInt("notif_hour", value) }
        _notificationHour.value = value
    }

    fun setNotificationMinute(value: Int) {
        prefs.edit { putInt("notif_minute", value) }
        _notificationMinute.value = value
    }

    fun setFontSize(value: Float) {
        prefs.edit { putFloat("font_size", value) }
        _fontSize.value = value
    }

    fun setLastBook(value: Int) {
        prefs.edit { putInt("last_book", value) }
        _lastBook.value = value
    }

    fun setLastChapter(value: Int) {
        prefs.edit { putInt("last_chapter", value) }
        _lastChapter.value = value
    }

    fun setConfession(value: String) {
        prefs.edit { putString("confession", value) }
        _confession.value = value
    }

    fun setNightMode(value: Int) {
        prefs.edit { putInt("night_mode", value) }
        _nightMode.value = value
    }
}