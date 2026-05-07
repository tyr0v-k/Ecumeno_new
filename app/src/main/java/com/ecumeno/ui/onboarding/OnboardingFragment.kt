package com.ecumeno.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ecumeno.EcumenoApp
import com.ecumeno.R
import com.ecumeno.data.local.preferences.Confession
import com.ecumeno.data.local.preferences.PreferencesRepository
import com.ecumeno.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesRepository: PreferencesRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesRepository = (requireActivity().application as EcumenoApp).preferencesRepository
        val radioGroup = binding.radioGroupOptions
        val buttonContinue = binding.buttonContinue

        buttonContinue.setOnClickListener {
            val selectedValue = when (radioGroup.checkedRadioButtonId) {
                R.id.option1 -> Confession.ort
                R.id.option2 -> Confession.cat
                R.id.option3 -> Confession.lut
                else -> Confession.ort
            }

            preferencesRepository.setConfession(Confession.toPreferences(selectedValue))
            findNavController().navigate(OnboardingFragmentDirections.actionOnboardingToCalendar())
            requireActivity().recreate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}