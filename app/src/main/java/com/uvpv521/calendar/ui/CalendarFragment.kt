package com.uvpv521.calendar.ui

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.view.GestureDetector
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
import androidx.recyclerview.widget.GridLayoutManager
import com.uvpv521.calendar.R
import com.uvpv521.calendar.data.models.FastLevel
import com.uvpv521.calendar.databinding.FragmentCalendarBinding
import com.uvpv521.calendar.ui.viewmodels.CalendarViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    // Используем viewModels() делегат для создания ViewModel
    private val viewModel: CalendarViewModel by viewModels()

    private lateinit var calendarAdapter: CalendarAdapter

    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var sensorManager: SensorManager

    private var current = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        initGestureDetector()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun setupRecyclerView() {
        calendarAdapter = CalendarAdapter { date ->
            viewModel.selectDate(date)
        }

        binding.calendarGrid.apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = calendarAdapter
        }
        binding.calendarGrid.isNestedScrollingEnabled = false
    }

    private fun setupObservers() {
        // Подписываемся на StateFlow с помощью lifecycleScope
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Наблюдаем за calendarDays
                viewModel.calendarDays.collect { days ->
                    calendarAdapter.submitList(days)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Наблюдаем за currentMonth
                viewModel.currentMonth.collect { month ->
                    val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale.getDefault())
                    binding.monthTitle.text = month.format(formatter).substring(0,1).uppercase() + month.format(formatter).substring(1)
                    if(month == YearMonth.now()){
                        binding.prevMonth.imageAlpha = 100
                        binding.prevMonth.isEnabled = false
                        current = true
                        binding.todayButton.isEnabled = false
                        binding.todayButton.setAlpha(0.1f)
                    }
                    else{
                        binding.prevMonth.imageAlpha = 1000
                        binding.prevMonth.isEnabled = true
                        current = false
                        binding.todayButton.isEnabled = true
                        binding.todayButton.setAlpha(1f)
                    }
                    calendarAdapter.setCurrentMonth(month.month)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Наблюдаем за selectedDate
                viewModel.selectedDate.collect { date ->
                    date?.let { showDateDetails(it) }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.prevMonth.setOnClickListener {
            viewModel.navigateToPreviousMonth()
        }

        binding.nextMonth.setOnClickListener {
            viewModel.navigateToNextMonth()
        }

        binding.todayButton.setOnClickListener {
            viewModel.navigateToToday()
        }
    }

    private fun showDateDetails(date: LocalDate) {
        // Получаем текущий список дней
        val days = calendarAdapter.currentList
        val day = days.find { it.date == date }

        day?.let {
            binding.dayInfo.visibility=View.VISIBLE
            binding.selectedDate.text = date.format(
                DateTimeFormatter.ofPattern("dd MMMM yyyy, EEEE", Locale.getDefault())
            )

            if (it.holidays.isNotEmpty()) {
                binding.holidayName.text = it.holidays.joinToString("\n") { holiday -> getString(requireContext().resources.getIdentifier(holiday.name, "string", requireContext().packageName)) }
                binding.holidayName.visibility = View.VISIBLE
            } else {
                binding.holidayName.visibility = View.GONE
            }

            // Отображение поста
            val fastText = when (it.fastLevel) {
                FastLevel.NO_FAST -> getString(R.string.fast_no_fast)
                FastLevel.CONTINUOUS_WEEK -> getString(R.string.fast_continuous_week)
                FastLevel.XEROPHAGY -> getString(R.string.fast_xerophagy)
                FastLevel.NO_FISH -> getString(R.string.fast_no_fish)
                FastLevel.NO_OIL -> getString(R.string.fast_no_oil)
                FastLevel.FAST -> getString(R.string.fast_strict)
                FastLevel.WINE_OIL_ALLOWED -> getString(R.string.fast_wine_oil_allowed)
            }
            binding.fastInfo.text = fastText
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                        viewModel.navigateToNextMonth()
                    } else if (!current){
                        viewModel.navigateToPreviousMonth()
                    }
                    return true
                }
                return false
            }

            override fun onDown(e: MotionEvent): Boolean = true
        }
        )
    }
}