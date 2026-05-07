package com.ecumeno.ui.rosary

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ecumeno.EcumenoApp
import com.ecumeno.R
import com.ecumeno.ui.rosary.enums.MysteryType
import com.ecumeno.ui.rosary.enums.PrayerType
import com.ecumeno.databinding.FragmentRosaryBinding
import com.ecumeno.di.ViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class RosaryFragment : Fragment() {
    private var _binding: FragmentRosaryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RosaryViewModel by viewModels {
        ViewModelFactory((requireActivity().application as EcumenoApp).preferencesRepository)
    }
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var vibrator: Vibrator

    private var delimiter = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRosaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        initGestureDetector()
        setupObservers()

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_VOLUME_UP -> {
                        viewModel.nextBead()
                        triggerVibration()
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_VOLUME_DOWN -> {
                        viewModel.previousBead()
                        triggerVibration()
                        return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
        }

        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        binding.btnReset.setOnClickListener { viewModel.resetRosary() ; triggerVibration() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGestureDetector() {
        gestureDetector = GestureDetectorCompat(
            requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    val diffX = e2.x - (e1?.x ?: 0f)
                    if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                        if (diffX < 0) {
                            viewModel.nextBead()
                            triggerVibration()
                        } else {
                            viewModel.previousBead()
                            triggerVibration()
                        }
                        return true
                    }
                    return false
                }

                override fun onDown(e: MotionEvent): Boolean = true
            })
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.tvPrayer.text = getPrayerText(state.prayerType)
                    binding.tvPrayer.visibility = if (state.prayerVisibility) View.VISIBLE else View.INVISIBLE
                    binding.tvBeadNumber.text = (if (state.decadeVisibility) "${getString(R.string.decade)}: ${state.currentDecade}\n" else "") + "${if (state.beadVisibility) "${getString(
                        R.string.bead)}: ${state.displayBead}" else ""}"
                    binding.tvMysteryTitle.text = getMysteryText(state.mysteryType)
                    binding.tvMysteryTitle.visibility = if(state.mysteryVisibility) View.VISIBLE else View.GONE
                    if (!state.hasCenterPiece) binding.beadsView.setBigCircleIndex(state.currentBead)
                    delimiter = state.isDelimiter
                }
            }
        }
    }

    private fun getPrayerText(prayerType: PrayerType): String {
        return when (prayerType) {
            PrayerType.CREED -> getString(R.string.rosary_creed)
            PrayerType.OUR_FATHER -> getString(R.string.rosary_our_father)
            PrayerType.HAIL_MARY -> getString(R.string.rosary_hail_mary)
            PrayerType.GLORY_BE -> getString(R.string.rosary_glory)
            PrayerType.FATIMA_PRAYER -> getString(R.string.rosary_fatima)
            PrayerType.JESUS_PRAYER -> getString(R.string.rosary_jesus)
            PrayerType.SILENCE -> ""
            PrayerType.FRALSARKRANSEN_FIRST -> getString(R.string.fralsarkransen_first)
            PrayerType.FRALSARKRANSEN_SECOND -> getString(R.string.fralsarkransen_second)
            PrayerType.FRALSARKRANSEN_THIRD -> getString(R.string.fralsarkransen_third)
            PrayerType.FRALSARKRANSEN_FOURTH -> getString(R.string.fralsarkransen_fourth)
            PrayerType.FRALSARKRANSEN_FIFTH -> getString(R.string.fralsarkransen_fifth)
            PrayerType.FRALSARKRANSEN_SIXTH -> getString(R.string.fralsarkransen_sixth)
            PrayerType.FRALSARKRANSEN_SEVENTH -> getString(R.string.fralsarkransen_seventh)
            PrayerType.FRALSARKRANSEN_EIGHT -> getString(R.string.fralsarkransen_eight)
            PrayerType.FRALSARKRANSEN_NINTH -> getString(R.string.fralsarkransen_ninth)
            PrayerType.FRALSARKRANSEN_TENTH -> getString(R.string.fralsarkransen_tenth)
        }
    }

    private fun getMysteryText(mysteryType: MysteryType): String {
        return when (mysteryType){
            MysteryType.JOYFUL -> "${getString(R.string.rosary_mystery)}: ${getString(R.string.rosary_mystery_joyful)}"
            MysteryType.GLORIOUS -> "${getString(R.string.rosary_mystery)}: ${getString(R.string.rosary_mystery_glorious)}"
            MysteryType.LUMINOUS -> "${getString(R.string.rosary_mystery)}: ${getString(R.string.rosary_mystery_luminous)}"
            MysteryType.SORROWFUL -> "${getString(R.string.rosary_mystery)}: ${getString(R.string.rosary_mystery_sorrowful)}"
        }
    }

    private fun triggerVibration() {
        if (delimiter){
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        else{
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}