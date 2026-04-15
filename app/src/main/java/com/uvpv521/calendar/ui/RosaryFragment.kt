package com.uvpv521.calendar.ui

import android.content.Context
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
import android.widget.Button
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.uvpv521.calendar.R
import com.uvpv521.calendar.data.models.PrayerType
import com.uvpv521.calendar.databinding.FragmentRosaryBinding
import java.util.Calendar

class RosaryFragment : Fragment() {
    private var _binding: FragmentRosaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var tvPrayer: TextView
    private lateinit var tvBeadNumber: TextView
    private lateinit var tvMysteryTitle: TextView
    private lateinit var btnReset: Button

    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var vibrator: Vibrator
    private lateinit var sensorManager: SensorManager

    private var currentDecade = 0
    private var currentBead = 0
    private var currentPrayerIndex = 0
    private var delimiter = false

    private val rosaryStructure = listOf(
        PrayerType.CREED,          // 0: Символ веры
        PrayerType.OUR_FATHER,     // 1: Отче наш
        PrayerType.HAIL_MARY,      // 2-4: 3 раза Радуйся, Мария
        PrayerType.HAIL_MARY,
        PrayerType.HAIL_MARY,
        PrayerType.GLORY_BE,       // 5: Слава
        // Декада 1
        PrayerType.OUR_FATHER,     // 7: Отче наш
        PrayerType.HAIL_MARY,      // 8-17: 10 раз Радуйся, Мария
        PrayerType.HAIL_MARY,
        PrayerType.HAIL_MARY,
        PrayerType.HAIL_MARY,
        PrayerType.HAIL_MARY,
        PrayerType.HAIL_MARY,
        PrayerType.HAIL_MARY,
        PrayerType.HAIL_MARY,
        PrayerType.HAIL_MARY,
        PrayerType.HAIL_MARY,
        PrayerType.GLORY_BE,       // 18: Слава
        PrayerType.FATIMA_PRAYER,  // 19: Фатимская молитва
        // Декады 2-5 повторяют структуру 8-19
    )

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

        initViews()
        initGestureDetector()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
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
    private fun initViews() {
        tvPrayer = binding.tvPrayer
        tvBeadNumber = binding.tvBeadNumber
        tvMysteryTitle = binding.tvMysteryTitle
        btnReset = binding.btnReset

        vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        btnReset.setOnClickListener { resetRosary() }
    }

    private fun initGestureDetector() {
        gestureDetector = GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2.x - (e1?.x ?: 0f)
                if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                    if (diffX < 0) {
                        // Свайп влево - следующий шаг
                        nextBead()
                    } else {
                        // Свайп вправо - предыдущий шаг
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
        if(currentDecade == 5 && currentPrayerIndex == rosaryStructure.size - 1){
            resetRosary()
        }
        else{
            if (currentPrayerIndex < rosaryStructure.size - 1) {
                if(currentPrayerIndex == 5){
                    currentDecade++
                }
                currentPrayerIndex++
            }
            else{
                currentPrayerIndex = 6
                currentDecade++
            }
            updateCurrentDecadeAndBead()
            updateDisplay()
            triggerVibration(delimiter)
        }
    }

    private fun previousBead() {
        if (currentPrayerIndex > 0 && currentBead > 1) {
            currentPrayerIndex--
            updateCurrentDecadeAndBead()
            updateDisplay()
            triggerVibration(delimiter)
        }
    }

    private fun updateCurrentDecadeAndBead() {
        // Логика определения декады и зерна
        when {
            currentPrayerIndex == 0 -> { currentDecade = 0; currentBead = 0 }
            currentPrayerIndex <= 5 -> { currentDecade = 0; currentBead = currentPrayerIndex }
            else -> {
                val beadInDecade = (currentPrayerIndex - 5) % 14
                currentBead = beadInDecade
            }
        }
    }

    private fun updateDisplay() {
        val mysteries = listOf(
            getString(R.string.rosary_mystery_joyful),
            getString(R.string.rosary_mystery_luminous),
            getString(R.string.rosary_mystery_sorrowful),
            getString(R.string.rosary_mystery_glorious)
        )
        val prayerType = rosaryStructure[currentPrayerIndex]
        if (getPrayerText(prayerType) != tvPrayer.text.toString()){
            delimiter = true
        }
        else{
            delimiter = false
        }
        tvPrayer.text = getPrayerText(prayerType)
        tvBeadNumber.text = "${getString(R.string.decade)}: ${currentDecade}, ${getString(R.string.bead)}: ${currentBead}"

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
        tvMysteryTitle.text = "${getString(R.string.rosary_mystery)}: ${mysteries[dayOfWeek]}"
    }

    private fun getPrayerText(prayerType: PrayerType): String {
        val prayers = listOf(
            getString(R.string.rosary_creed),
            getString(R.string.rosary_our_father),
            getString(R.string.rosary_hail_mary),
            getString(R.string.rosary_glory),
            getString(R.string.rosary_fatima)
        )
        return when (prayerType) {
            PrayerType.CREED -> prayers[0]
            PrayerType.OUR_FATHER -> prayers[1]
            PrayerType.HAIL_MARY -> prayers[2]
            PrayerType.GLORY_BE -> prayers[3]
            PrayerType.FATIMA_PRAYER -> prayers[4]
        }
    }

    private fun triggerVibration(delimiter : Boolean) {
        if (delimiter){
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        else{
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun resetRosary() {
        currentPrayerIndex = 0
        currentDecade = 0
        currentBead = 0
        delimiter = false
        updateDisplay()
        triggerVibration(delimiter)
    }
}