package com.ecumeno.ui.rosary

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.ecumeno.R
import com.ecumeno.data.local.preferences.PrefsHelper
import com.ecumeno.core.utils.models.enums.PrayerType
import com.ecumeno.databinding.FragmentRosaryBinding
import java.util.Calendar

class RosaryFragment : Fragment() {
    private var _binding: FragmentRosaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var beadsView: BeadsView
    private lateinit var tvPrayer: TextView
    private lateinit var tvBeadNumber: TextView
    private lateinit var tvMysteryTitle: TextView
    private lateinit var btnReset: Button

    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var vibrator: Vibrator
    private lateinit var sensorManager: SensorManager
    private lateinit var prefs: PrefsHelper
    private lateinit var rosaryStructure : List<PrayerType>
    private var rosaryStart = 1
    private var rosaryLimit = 0

    private var currentDecade = 0
    private var currentBead = 0
    private var currentPrayerIndex = 0
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

        prefs = PrefsHelper(requireContext())
        initRosaryStructure()
        initViews()
        initGestureDetector()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (prefs.confession == "cat" && prefs.isRuleEnabled){
            initMysteries()
        } else if (!(prefs.confession == "lut" && prefs.isRuleEnabled)){
            currentDecade++
        }
        updateDisplay()

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_VOLUME_UP -> {
                        nextBead()
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_VOLUME_DOWN -> {
                        previousBead()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    private fun initViews() {
        tvPrayer = binding.tvPrayer
        tvBeadNumber = binding.tvBeadNumber
        btnReset = binding.btnReset
        beadsView = binding.beadsView
        if (!prefs.isRuleEnabled){
            tvPrayer.visibility = View.INVISIBLE
        }
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        btnReset.setOnClickListener { resetRosary() }
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
                            nextBead()
                        } else {
                            previousBead()
                        }
                        return true
                    }
                    return false
                }

                override fun onDown(e: MotionEvent): Boolean = true
            })
    }

    private fun nextBead() {
        if(currentDecade == rosaryLimit && currentPrayerIndex == rosaryStructure.size - 1){
            resetRosary()
        }
        else{
            if (prefs.confession == "cat" && prefs.isRuleEnabled){
                if (currentPrayerIndex < rosaryStructure.size - 1) {
                    if(currentPrayerIndex == rosaryLimit){
                        currentDecade++
                    }
                    currentPrayerIndex++
                }
                else{
                    currentPrayerIndex = rosaryLimit + 1
                    currentDecade++
                }
            } else{
                if (currentPrayerIndex < rosaryStructure.size - 1) {
                    currentPrayerIndex++
                }
                else{
                    currentDecade++
                    currentPrayerIndex = 0
                }
            }
            updateCurrentDecadeAndBead()
            updateDisplay()
            triggerVibration()
        }
    }

    private fun previousBead() {
        if (currentPrayerIndex > 0) {
            currentPrayerIndex--
            updateCurrentDecadeAndBead()
            updateDisplay()
            triggerVibration()
        }
    }

    private fun updateCurrentDecadeAndBead() {
        if (prefs.confession == "cat" && prefs.isRuleEnabled){
            when {
                currentPrayerIndex == 0 -> { currentDecade = 0; currentBead = 0 }
                currentPrayerIndex <= 5 -> { currentDecade = 0; currentBead = currentPrayerIndex }
                else -> {
                    val beadInDecade = (currentPrayerIndex - 5) % 14
                    currentBead = beadInDecade
                }
            }
        }
        else{
            currentBead = currentPrayerIndex
        }
    }

    private fun updateDisplay() {
        val prayerType = rosaryStructure[currentPrayerIndex]
        if (getPrayerText(prayerType) != tvPrayer.text.toString()){
            delimiter = true
        }
        else{
            delimiter = false
        }
        tvPrayer.text = getPrayerText(prayerType)
        if (prefs.confession == "lut" && prefs.isRuleEnabled) currentBead++
        tvBeadNumber.text = "${if (currentDecade != 0 && !(prefs.confession == "lut" && prefs.isRuleEnabled)) "${getString(
            R.string.decade)}: ${currentDecade}\n" else ""}" +
                "${if (currentBead != 0 && !(prefs.confession == "cat" && currentPrayerIndex > 16)) "${getString(
                    R.string.bead)}: ${currentBead}" else ""}"
        if (!(prefs.confession == "cat" && currentPrayerIndex > 16)) beadsView.setBigCircleIndex(if(prefs.confession != "cat" || (prefs.confession == "cat" && !prefs.isRuleEnabled) || (prefs.confession == "cat" && prefs.isRuleEnabled && currentPrayerIndex > 5)) currentBead + 10 else currentBead)
    }

    private fun initMysteries(){
        tvMysteryTitle = binding.tvMysteryTitle
        val mysteries = listOf(
            getString(R.string.rosary_mystery_joyful),
            getString(R.string.rosary_mystery_luminous),
            getString(R.string.rosary_mystery_sorrowful),
            getString(R.string.rosary_mystery_glorious)
        )
        // Определяем тайну по дню недели
        val dayOfWeek = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.SATURDAY -> 0
            Calendar.TUESDAY -> 2
            Calendar.FRIDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.SUNDAY -> 3
            Calendar.THURSDAY -> 1
            else -> 0
        }
        tvMysteryTitle.visibility = View.VISIBLE
        tvMysteryTitle.text = "${getString(R.string.rosary_mystery)}: ${mysteries[dayOfWeek]}"
    }

    private fun initRosaryStructure(){
        if (prefs.confession == "cat" && prefs.isRuleEnabled){
            rosaryStructure = listOf(
                PrayerType.CREED,
                PrayerType.OUR_FATHER,
                PrayerType.HAIL_MARY,
                PrayerType.HAIL_MARY,
                PrayerType.HAIL_MARY,
                PrayerType.GLORY_BE,
                PrayerType.OUR_FATHER,
                PrayerType.HAIL_MARY,
                PrayerType.HAIL_MARY,
                PrayerType.HAIL_MARY,
                PrayerType.HAIL_MARY,
                PrayerType.HAIL_MARY,
                PrayerType.HAIL_MARY,
                PrayerType.HAIL_MARY,
                PrayerType.HAIL_MARY,
                PrayerType.HAIL_MARY,
                PrayerType.HAIL_MARY,
                PrayerType.GLORY_BE,
                PrayerType.FATIMA_PRAYER,
            )
            rosaryStart = 0
            rosaryLimit = 5
        } else if (prefs.confession == "lut" && prefs.isRuleEnabled){
            rosaryStructure = listOf(
                PrayerType.FRALSARKRANSEN_FIRST,
                PrayerType.FRALSARKRANSEN_SECOND,
                PrayerType.FRALSARKRANSEN_THIRD,
                PrayerType.FRALSARKRANSEN_FOURTH,
                PrayerType.FRALSARKRANSEN_SECOND,
                PrayerType.FRALSARKRANSEN_FIFTH,
                PrayerType.FRALSARKRANSEN_SECOND,
                PrayerType.FRALSARKRANSEN_SIXTH,
                PrayerType.FRALSARKRANSEN_SECOND,
                PrayerType.FRALSARKRANSEN_SEVENTH,
                PrayerType.FRALSARKRANSEN_EIGHT,
                PrayerType.SILENCE,
                PrayerType.SILENCE,
                PrayerType.SILENCE,
                PrayerType.FRALSARKRANSEN_NINTH,
                PrayerType.FRALSARKRANSEN_SECOND,
                PrayerType.FRALSARKRANSEN_TENTH,
                PrayerType.FRALSARKRANSEN_SECOND
            )
        }
        else {
            rosaryStructure = listOf(
                PrayerType.OUR_FATHER,     // 1: Отче наш
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER,
                PrayerType.JESUS_PRAYER
            )
            rosaryLimit = 3
        }
    }
    private fun getPrayerText(prayerType: PrayerType): String {
        val prayers = listOf(
            getString(R.string.rosary_creed),
            getString(R.string.rosary_our_father),
            getString(R.string.rosary_hail_mary),
            getString(R.string.rosary_glory),
            getString(R.string.rosary_fatima),
            getString(R.string.rosary_jesus),
            getString(R.string.fralsarkransen_first),
            getString(R.string.fralsarkransen_second),
            getString(R.string.fralsarkransen_third),
            getString(R.string.fralsarkransen_fourth),
            getString(R.string.fralsarkransen_fifth),
            getString(R.string.fralsarkransen_sixth),
            getString(R.string.fralsarkransen_seventh),
            getString(R.string.fralsarkransen_eight),
            getString(R.string.fralsarkransen_ninth),
            getString(R.string.fralsarkransen_tenth)
        )
        return when (prayerType) {
            PrayerType.CREED -> prayers[0]
            PrayerType.OUR_FATHER -> prayers[1]
            PrayerType.HAIL_MARY -> prayers[2]
            PrayerType.GLORY_BE -> prayers[3]
            PrayerType.FATIMA_PRAYER -> prayers[4]
            PrayerType.JESUS_PRAYER -> prayers[5]
            PrayerType.SILENCE -> ""
            PrayerType.FRALSARKRANSEN_FIRST -> prayers[6]
            PrayerType.FRALSARKRANSEN_SECOND -> prayers[7]
            PrayerType.FRALSARKRANSEN_THIRD -> prayers[8]
            PrayerType.FRALSARKRANSEN_FOURTH -> prayers[9]
            PrayerType.FRALSARKRANSEN_FIFTH -> prayers[10]
            PrayerType.FRALSARKRANSEN_SIXTH -> prayers[11]
            PrayerType.FRALSARKRANSEN_SEVENTH -> prayers[12]
            PrayerType.FRALSARKRANSEN_EIGHT -> prayers[13]
            PrayerType.FRALSARKRANSEN_NINTH -> prayers[14]
            PrayerType.FRALSARKRANSEN_TENTH -> prayers[15]
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

    private fun resetRosary() {
        currentPrayerIndex = 0
        currentDecade = rosaryStart
        currentBead = 0
        delimiter = false
        updateDisplay()
        triggerVibration()
    }
}