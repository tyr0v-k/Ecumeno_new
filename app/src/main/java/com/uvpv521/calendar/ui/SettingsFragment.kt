package com.uvpv521.calendar.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.uvpv521.calendar.databinding.FragmentSettingsBinding
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = requireContext().resources
        val configuration = resources.configuration
        configuration.setLocale(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Сохраняем выбранный язык
        saveLocalePreference(languageCode)
        context?.getDatabasePath("bible.db")?.delete()
        context?.getDatabasePath("prayers_ort.db")?.delete()
        // Перезапускаем активность для применения изменений
        requireActivity().recreate()
    }

    private fun saveLocalePreference(languageCode: String) {
        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_language", languageCode).apply()
    }
}