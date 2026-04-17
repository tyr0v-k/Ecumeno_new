package com.uvpv521.calendar.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.uvpv521.calendar.data.local.PrefsHelper
import com.uvpv521.calendar.databinding.FragmentSettingsBinding
import com.uvpv521.calendar.notifications.AlarmUtils
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefs: PrefsHelper

    // 1. Инициализация лаунчера для запроса разрешения на уведомления (Android 13+)
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkNotificationPermission()
        } else {
            // Если отказали, возвращаем свитч в выключенное положение
            binding.switchNotifications.isChecked = false
            prefs.isNotificationEnabled = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsHelper(requireContext())

        binding.switchNotifications.isChecked = prefs.isNotificationEnabled
        binding.switchRosary.isChecked = prefs.isRuleEnabled
        updateTimeButtonText(prefs.notificationHour, prefs.notificationMinute)

        binding.switchNotifications.setOnClickListener {
            val isChecked = binding.switchNotifications.isChecked
            if (isChecked) {
                checkNotificationPermission()
            } else {
                disableNotifications()
            }
        }

        binding.switchRosary.setOnClickListener {
            val isChecked = binding.switchRosary.isChecked
            if (isChecked) {
                prefs.isRuleEnabled = true
            } else {
                prefs.isRuleEnabled = false
            }
        }


        binding.notificationTime.setOnClickListener {
            showTimePicker()
        }

        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            // Логика переключения темы
        }

        // Добавляем кнопки для выбора языка
        binding.buttonRussian.setOnClickListener {
            setLocale("ru")
        }

        binding.buttonEnglish.setOnClickListener {
            setLocale("en")
        }

        binding.buttonOrt.setOnClickListener {
            prefs.confession = "ort"
            context?.getDatabasePath("prayers.db")?.delete()
        }

        binding.buttonCat.setOnClickListener {
            prefs.confession = "cat"
            context?.getDatabasePath("prayers.db")?.delete()
        }

        binding.buttonLut.setOnClickListener {
            prefs.confession = "lut"
            context?.getDatabasePath("prayers.db")?.delete()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLocale(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        context?.getDatabasePath("bible.db")?.delete()
        context?.getDatabasePath("prayers.db")?.delete()
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    // 2. Проверка разрешения на отправку уведомлений
    private fun checkNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Разрешение уже есть
                enableNotifications()
            }
            else -> {
                // Запрашиваем разрешение
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun enableNotifications() {
        prefs.isNotificationEnabled = true
        AlarmUtils.scheduleNotification(requireContext(), prefs.notificationHour, prefs.notificationMinute)
    }

    private fun disableNotifications() {
        prefs.isNotificationEnabled = false
        AlarmUtils.cancelNotification(requireContext())
    }

    private fun showTimePicker() {
        val picker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                prefs.notificationHour = hourOfDay
                prefs.notificationMinute = minute
                updateTimeButtonText(hourOfDay, minute)

                if (prefs.isNotificationEnabled) {
                    AlarmUtils.scheduleNotification(requireContext(), hourOfDay, minute)
                }
            },
            prefs.notificationHour,
            prefs.notificationMinute,
            true
        )
        picker.show()
    }

    private fun updateTimeButtonText(hour: Int, minute: Int) {
        val timeString = String.format("%02d:%02d", hour, minute)
        binding.notificationTime.text = "$timeString"
    }
}