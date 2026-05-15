package com.ecumeno.ui.settings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ecumeno.EcumenoApp
import com.ecumeno.core.domain.Confession
import com.ecumeno.infrastructure.notifications.AlarmUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesRepository = (application as EcumenoApp).preferencesRepository
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        val isNotificationEnabled = preferencesRepository.isNotificationEnabled.value
        val isRuleEnabled = preferencesRepository.isRuleEnabled.value
        val hour = preferencesRepository.notificationHour.value
        val minute = preferencesRepository.notificationMinute.value
        val nightMode = preferencesRepository.nightMode.value
        val confession = Confession.fromPreferences(preferencesRepository.confession.value)
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val languageCode = if (currentLocales.isEmpty) {
            Locale.getDefault().language
        } else {
            currentLocales[0]?.language ?: "en"
        }

        _uiState.value = SettingsUiState(
            isNotificationEnabled = isNotificationEnabled,
            isRuleEnabled = isRuleEnabled,
            notificationTime = String.format("%02d:%02d", hour, minute),
            notificationHour = hour,
            notificationMinute = minute,
            nightMode = nightMode,
            selectedThemePosition = when (nightMode) {
                AppCompatDelegate.MODE_NIGHT_YES -> 1
                AppCompatDelegate.MODE_NIGHT_NO -> 2
                else -> 0
            },
            confession = confession,
            selectedConfessionPosition = when (confession) {
                Confession.ort -> 0
                Confession.cat -> 1
                Confession.lut -> 2
            },
            selectedLanguagePosition = if (languageCode == "ru") 1 else 0,
            currentLanguageCode = languageCode
        )
    }

    fun onNotificationSwitchChanged(isChecked: Boolean) {
        _uiState.value = _uiState.value.copy(isNotificationEnabled = isChecked)
        if (!isChecked) {
            preferencesRepository.setNotificationEnabled(false)
            AlarmUtils.cancelNotification(getApplication())
        }
    }

    fun onNotificationPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            preferencesRepository.setNotificationEnabled(true)
            val state = _uiState.value
            AlarmUtils.scheduleNotification(getApplication(), state.notificationHour, state.notificationMinute)
            _uiState.value = _uiState.value.copy(isNotificationEnabled = true)
        } else {
            _uiState.value = _uiState.value.copy(isNotificationEnabled = false)
            preferencesRepository.setNotificationEnabled(false)
        }
    }

    fun onRuleSwitchChanged(isChecked: Boolean) {
        _uiState.value = _uiState.value.copy(isRuleEnabled = isChecked)
        preferencesRepository.setRuleEnabled(isChecked)
    }

    fun onThemeChanged(position: Int) {
        val nightMode = when (position) {
            1 -> AppCompatDelegate.MODE_NIGHT_YES
            2 -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(nightMode)
        preferencesRepository.setNightMode(nightMode)
        _uiState.value = _uiState.value.copy(nightMode = nightMode, selectedThemePosition = position)
    }

    fun onConfessionChanged(position: Int) {
        viewModelScope.launch {
            val confession = when (position) {
                0 -> Confession.ort
                1 -> Confession.cat
                2 -> Confession.lut
                else -> Confession.ort
            }

            preferencesRepository.setConfession(Confession.toPreferences(confession))
            getApplication<Application>().deleteDatabase("prayers.db")
            preferencesRepository.setPrayersCleared(true)
            _uiState.value = _uiState.value.copy(
                confession = confession,
                selectedConfessionPosition = position
            )
        }
    }

    fun onLanguageChanged(position: Int) {
        viewModelScope.launch {
            val languageCode = if (position == 1) "ru" else "en"
            val appLocale = LocaleListCompat.forLanguageTags(languageCode)
            getApplication<Application>().deleteDatabase("bible.db")
            getApplication<Application>().deleteDatabase("prayers.db")
            AppCompatDelegate.setApplicationLocales(appLocale)
            preferencesRepository.setBibleCleared(true)
            preferencesRepository.setPrayersCleared(true)
            _uiState.value = _uiState.value.copy(
                selectedLanguagePosition = position,
                currentLanguageCode = languageCode
            )
        }
    }

    fun onNotificationTimeChanged(hourOfDay: Int, minute: Int) {
        preferencesRepository.setNotificationTime(hourOfDay, minute)
        val timeString = String.format("%02d:%02d", hourOfDay, minute)
        _uiState.value = _uiState.value.copy(
            notificationHour = hourOfDay,
            notificationMinute = minute,
            notificationTime = timeString
        )
        if (_uiState.value.isNotificationEnabled) {
            AlarmUtils.scheduleNotification(getApplication(), hourOfDay, minute)
        }
    }
}