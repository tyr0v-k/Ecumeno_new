package com.ecumeno.data.local.preferences

import kotlinx.coroutines.flow.StateFlow

class PreferencesRepository(private val prefsHelper: PrefsHelper) {
    val isNotificationEnabled: StateFlow<Boolean> = prefsHelper.isNotificationEnabled
    val isRuleEnabled: StateFlow<Boolean> = prefsHelper.isRuleEnabled
    val notificationHour: StateFlow<Int> = prefsHelper.notificationHour
    val notificationMinute: StateFlow<Int> = prefsHelper.notificationMinute
    val fontSize: StateFlow<Float> = prefsHelper.fontSize
    val lastBook: StateFlow<Int> = prefsHelper.lastBook
    val lastChapter: StateFlow<Int> = prefsHelper.lastChapter
    val confession: StateFlow<String> = prefsHelper.confession
    val nightMode: StateFlow<Int> = prefsHelper.nightMode

    fun setNotificationEnabled(value: Boolean) = prefsHelper.setNotificationEnabled(value)
    fun setRuleEnabled(value: Boolean) = prefsHelper.setRuleEnabled(value)
    fun setNotificationTime(hour: Int, minute: Int) {
        prefsHelper.setNotificationHour(hour)
        prefsHelper.setNotificationMinute(minute)
    }
    fun clearReadingProgress() {
        prefsHelper.setLastChapter(-1)
        prefsHelper.setLastBook(-1)
    }
    fun setFontSize(value: Float) = prefsHelper.setFontSize(value)
    fun setLastBook(value: Int) = prefsHelper.setLastBook(value)
    fun setLastChapter(value: Int) = prefsHelper.setLastChapter(value)
    fun setConfession(value: String) = prefsHelper.setConfession(value)
    fun setNightMode(value: Int) = prefsHelper.setNightMode(value)
}