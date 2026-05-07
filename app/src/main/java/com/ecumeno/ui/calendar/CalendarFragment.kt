package com.ecumeno.ui.calendar

import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.ecumeno.EcumenoApp
import com.ecumeno.R
import com.ecumeno.core.domain.CalendarDay
import com.ecumeno.core.domain.FastLevel
import com.ecumeno.databinding.FragmentCalendarBinding
import com.ecumeno.di.ViewModelFactory
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by viewModels {
        ViewModelFactory((requireActivity().application as EcumenoApp).preferencesRepository)
    }
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var gestureDetector: GestureDetectorCompat
    private var current = true
    private var hasFast = true

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    hasFast = state.hasFast
                    calendarAdapter.submitList(state.calendarDays)
                    val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale.forLanguageTag(getCurrentLocale()))
                    binding.monthTitle.text = state.currentMonth.format(formatter).substring(0,1).uppercase() + state.currentMonth.format(formatter).substring(1)
                    if(state.currentMonth == YearMonth.now()){
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
                    calendarAdapter.setCurrentDate(state.selectedDate)
                    state.selectedDate?.let { showDateDetails(it) }
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

    private fun showDateDetails(date: CalendarDay) {
        binding.dayInfo.visibility= View.VISIBLE
        binding.selectedDate.text = date.date.format(
            DateTimeFormatter.ofPattern("dd MMMM yyyy, EEEE", Locale.forLanguageTag(getCurrentLocale()))
        )

        if (date.holidays.isNotEmpty()) {
            binding.holidayName.text = date.holidays.joinToString("\n") { holiday -> getString(requireContext().resources.getIdentifier(holiday.name, "string", requireContext().packageName)) }
            binding.holidayName.visibility = View.VISIBLE
        } else {
            binding.holidayName.visibility = View.GONE
        }

        if (hasFast){
            val fastText = when (date.fastLevel) {
                FastLevel.NO_FAST -> getString(R.string.fast_no_fast)
                FastLevel.CONTINUOUS_WEEK -> getString(R.string.fast_continuous_week)
                FastLevel.XEROPHAGY -> getString(R.string.fast_xerophagy)
                FastLevel.FISH -> getString(R.string.fast_fish)
                FastLevel.NO_OIL -> getString(R.string.fast_no_oil)
                FastLevel.FAST -> getString(R.string.fast_strict)
                FastLevel.OIL_ALLOWED -> getString(R.string.fast_oil_allowed)
                FastLevel.ABSTINENCE -> getString(R.string.abstinence)
            }
            binding.fastInfo.text = fastText
        } else{
            binding.fastInfo.visibility = View.GONE
        }
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
                            viewModel.navigateToNextMonth()
                        } else if (!current) {
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

    private fun getCurrentLocale() : String{
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        if (currentLocales.isEmpty){
            return Locale.getDefault().language
        } else{
            return currentLocales[0]?.language ?: "en"
        }
    }
}