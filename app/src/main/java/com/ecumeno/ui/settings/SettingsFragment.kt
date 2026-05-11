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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ecumeno.R
import com.ecumeno.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        viewModel.onNotificationPermissionResult(isGranted)
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

        setupSpinners()
        setupClickListeners()
        setupObservers()
    }

    private fun setupSpinners() {
        val themeSpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayOf(
                getString(R.string.theme_system),
                getString(R.string.theme_nightmode),
                getString(R.string.theme_daymode)
            )
        )
        themeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTheme.adapter = themeSpinnerAdapter

        val confessionSpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayOf(
                getString(R.string.confession_ort),
                getString(R.string.confession_cat),
                getString(R.string.confession_lut)
            )
        )
        confessionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerConfession.adapter = confessionSpinnerAdapter

        val languageSpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayOf("English", "Русский")
        )
        languageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = languageSpinnerAdapter

        binding.spinnerTheme.post {
            binding.spinnerTheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.onThemeChanged(position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        binding.spinnerConfession.post {
            binding.spinnerConfession.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.onConfessionChanged(position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        binding.spinnerLanguage.post {
            binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.onLanguageChanged(position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun setupClickListeners() {
        binding.switchNotifications.setOnClickListener {
            if (binding.switchNotifications.isChecked) {
                checkNotificationPermission()
            } else {
                viewModel.onNotificationSwitchChanged(false)
            }
        }

        binding.switchRosary.setOnClickListener {
            viewModel.onRuleSwitchChanged(binding.switchRosary.isChecked)
        }

        binding.notificationTime.setOnClickListener {
            showTimePicker()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.switchNotifications.isChecked = state.isNotificationEnabled
                binding.switchRosary.isChecked = state.isRuleEnabled
                binding.notificationTime.text = state.notificationTime
                binding.spinnerTheme.setSelection(state.selectedThemePosition)
                binding.spinnerConfession.setSelection(state.selectedConfessionPosition)
                binding.spinnerLanguage.setSelection(state.selectedLanguagePosition)
            }
        }
    }

    private fun checkNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.onNotificationPermissionResult(true)
            }
            else -> {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showTimePicker() {
        val currentState = viewModel.uiState.value
        val picker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                viewModel.onNotificationTimeChanged(hourOfDay, minute)
            },
            currentState.notificationHour,
            currentState.notificationMinute,
            true
        )
        picker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}