package com.ecumeno.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
class PreferencesRepository(private val context: Context) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private object Keys {
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notif_enabled")
        val RULE_ENABLED = booleanPreferencesKey("rule_enabled")
        val NOTIFICATION_HOUR = intPreferencesKey("notif_hour")
        val NOTIFICATION_MINUTE = intPreferencesKey("notif_minute")
        val FONT_SIZE = floatPreferencesKey("font_size")
        val LAST_BOOK = intPreferencesKey("last_book")
        val LAST_CHAPTER = intPreferencesKey("last_chapter")
        val CONFESSION = stringPreferencesKey("confession")
        val NIGHT_MODE = intPreferencesKey("night_mode")
    }

    private val _isNotificationEnabled: MutableStateFlow<Boolean>
    private val _isRuleEnabled: MutableStateFlow<Boolean>
    private val _notificationHour: MutableStateFlow<Int>
    private val _notificationMinute: MutableStateFlow<Int>
    private val _fontSize: MutableStateFlow<Float>
    private val _lastBook: MutableStateFlow<Int>
    private val _lastChapter: MutableStateFlow<Int>
    private val _confession: MutableStateFlow<String>
    private val _nightMode: MutableStateFlow<Int>
    private val _bibleCleared = MutableStateFlow(false)
    private val _prayersCleared = MutableStateFlow(false)
    val isNotificationEnabled: StateFlow<Boolean> get() = _isNotificationEnabled.asStateFlow()
    val isRuleEnabled: StateFlow<Boolean> get() = _isRuleEnabled.asStateFlow()
    val notificationHour: StateFlow<Int> get() = _notificationHour.asStateFlow()
    val notificationMinute: StateFlow<Int> get() = _notificationMinute.asStateFlow()
    val fontSize: StateFlow<Float> get() = _fontSize.asStateFlow()
    val lastBook: StateFlow<Int> get() = _lastBook.asStateFlow()
    val lastChapter: StateFlow<Int> get() = _lastChapter.asStateFlow()
    val confession: StateFlow<String> get() = _confession.asStateFlow()
    val nightMode: StateFlow<Int> get() = _nightMode.asStateFlow()
    val bibleCleared: StateFlow<Boolean> get() = _bibleCleared.asStateFlow()
    val prayersCleared: StateFlow<Boolean> get() = _prayersCleared.asStateFlow()

    init {
        val prefs = runBlocking { context.dataStore.data.first() }
        _isNotificationEnabled = MutableStateFlow(prefs[Keys.NOTIFICATION_ENABLED] ?: false)
        _isRuleEnabled = MutableStateFlow(prefs[Keys.RULE_ENABLED] ?: true)
        _notificationHour = MutableStateFlow(prefs[Keys.NOTIFICATION_HOUR] ?: 9)
        _notificationMinute = MutableStateFlow(prefs[Keys.NOTIFICATION_MINUTE] ?: 0)
        _fontSize = MutableStateFlow(prefs[Keys.FONT_SIZE] ?: 18f)
        _lastBook = MutableStateFlow(prefs[Keys.LAST_BOOK] ?: -1)
        _lastChapter = MutableStateFlow(prefs[Keys.LAST_CHAPTER] ?: -1)
        _confession = MutableStateFlow(prefs[Keys.CONFESSION] ?: "")
        _nightMode = MutableStateFlow(prefs[Keys.NIGHT_MODE] ?: -1)

    }

    fun setNotificationEnabled(value: Boolean) {
        _isNotificationEnabled.value = value
        scope.launch { context.dataStore.edit { it[Keys.NOTIFICATION_ENABLED] = value } }
    }

    fun setRuleEnabled(value: Boolean) {
        _isRuleEnabled.value = value
        scope.launch { context.dataStore.edit { it[Keys.RULE_ENABLED] = value } }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        _notificationHour.value = hour
        _notificationMinute.value = minute
        scope.launch {
            context.dataStore.edit {
                it[Keys.NOTIFICATION_HOUR] = hour
                it[Keys.NOTIFICATION_MINUTE] = minute
            }
        }
    }

    fun clearReadingProgress() {
        setLastBook(-1)
        setLastChapter(-1)
    }

    fun setFontSize(value: Float) {
        _fontSize.value = value
        scope.launch { context.dataStore.edit { it[Keys.FONT_SIZE] = value } }
    }

    fun setLastBook(value: Int) {
        _lastBook.value = value
        scope.launch { context.dataStore.edit { it[Keys.LAST_BOOK] = value } }
    }

    fun setLastChapter(value: Int) {
        _lastChapter.value = value
        scope.launch { context.dataStore.edit { it[Keys.LAST_CHAPTER] = value } }
    }

    fun setConfession(value: String) {
        _confession.value = value
        scope.launch { context.dataStore.edit { it[Keys.CONFESSION] = value } }
    }

    fun setNightMode(value: Int) {
        _nightMode.value = value
        scope.launch { context.dataStore.edit { it[Keys.NIGHT_MODE] = value } }
    }

    fun setBibleCleared(value: Boolean) {
        _bibleCleared.value = value
    }

    fun setPrayersCleared(value: Boolean) {
        _prayersCleared.value = value
    }
}