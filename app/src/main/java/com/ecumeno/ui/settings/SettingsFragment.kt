package com.ecumeno.ui.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.ecumeno.R
import com.ecumeno.data.local.preferences.PrefsHelper
import com.ecumeno.databinding.FragmentSettingsBinding
import com.ecumeno.core.notifications.AlarmUtils
import java.util.Locale

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: PrefsHelper

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkNotificationPermission()
        } else {
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
        prefs = PrefsHelper(requireContext().applicationContext)
        binding.switchNotifications.isChecked = prefs.isNotificationEnabled
        binding.switchRosary.isChecked = prefs.isRuleEnabled
        updateTimeButtonText(prefs.notificationHour, prefs.notificationMinute)

        binding.switchNotifications.setOnClickListener {
            if (binding.switchNotifications.isChecked) {
                checkNotificationPermission()
            } else {
                disableNotifications()
            }
        }

        binding.switchRosary.setOnClickListener {
            prefs.isRuleEnabled = binding.switchRosary.isChecked
        }

        binding.notificationTime.setOnClickListener {
            showTimePicker()
        }

        val themeSpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayOf(getString(R.string.theme_system), getString(R.string.theme_nightmode), getString(R.string.theme_daymode)))
        themeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTheme.adapter = themeSpinnerAdapter
        when (prefs.nightMode){
            MODE_NIGHT_YES -> binding.spinnerTheme.setSelection(1)
            MODE_NIGHT_NO -> binding.spinnerTheme.setSelection(2)
            else -> binding.spinnerTheme.setSelection(0)
        }
        binding.spinnerTheme.post {
            binding.spinnerTheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (position){
                        0 -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                        1 -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                        2 -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                    }
                    prefs.nightMode = AppCompatDelegate.getDefaultNightMode()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        val confessionSpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayOf(getString(R.string.confession_ort), getString(R.string.confession_cat), getString(R.string.confession_lut)))
        confessionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerConfession.adapter = confessionSpinnerAdapter
        when (prefs.confession){
            "ort" -> binding.spinnerConfession.setSelection(0)
            "cat" -> binding.spinnerConfession.setSelection(1)
            "lut" -> binding.spinnerConfession.setSelection(2)
        }
        binding.spinnerConfession.post {
            binding.spinnerConfession.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (position){
                        0 -> prefs.confession = "ort"
                        1 -> prefs.confession = "cat"
                        2 -> prefs.confession = "lut"
                    }
                    requireContext().getDatabasePath("prayers.db")?.delete()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        val languageSpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayOf("English", "Русский"))
        languageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = languageSpinnerAdapter
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val languageCode : String
        if (currentLocales.isEmpty){
            languageCode = Locale.getDefault().language
        } else {
            languageCode = currentLocales[0]?.language ?: "en"
        }
        if (languageCode == "ru"){
            binding.spinnerLanguage.setSelection(1)
        } else {
            binding.spinnerConfession.setSelection(0)
        }
        binding.spinnerLanguage.post {
            binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (position){
                        0 -> setLocale("en")
                        1 -> setLocale("ru")
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLocale(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        requireContext().getDatabasePath("bible.db")?.delete()
        requireContext().getDatabasePath("prayers.db")?.delete()
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun checkNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableNotifications()
            }
            else -> {
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
        binding.notificationTime.text = "${String.format("%02d:%02d", hour, minute)}"
    }
}