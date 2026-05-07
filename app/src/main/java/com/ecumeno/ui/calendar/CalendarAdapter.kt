package com.ecumeno.ui.calendar

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ecumeno.R
import com.ecumeno.core.domain.CalendarDay
import com.ecumeno.core.domain.FastLevel
import com.ecumeno.databinding.CalendarDayItemBinding
import java.time.format.TextStyle
import java.util.Locale

class CalendarAdapter(private val onDateClick: (CalendarDay) -> Unit) : ListAdapter<CalendarDay, CalendarAdapter.CalendarViewHolder>(DiffCallback()) {
    private var selectedDate: CalendarDay? = null

    fun setCurrentDate(date: CalendarDay?) {
        selectedDate = date
    }
    inner class CalendarViewHolder(
        private val binding: CalendarDayItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: CalendarDay) {
            binding.dayNumber.text = day.date.dayOfMonth.toString()
            val currentLocales = AppCompatDelegate.getApplicationLocales()
            val languageCode : String
            if (currentLocales.isEmpty){
                languageCode = Locale.getDefault().language
            } else{
                languageCode = currentLocales[0]?.language ?: "en"
            }
            binding.dayOfWeek.text = day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag(languageCode))

            if (!day.currentMonth) {
                binding.dayNumber.setTextColor(
                    binding.root.context.getColor(R.color.not_available)
                )
                binding.holidayIndicator.visibility = View.GONE
                binding.fastIndicator.visibility = View.GONE
                binding.root.background = null
                binding.root.setOnClickListener(null)
            } else{
                if (day.date == selectedDate?.date) {
                    binding.root.setBackgroundResource(R.drawable.selected_day_bg)
                    binding.dayNumber.setTextColor(
                        binding.root.context.getColor(R.color.main)
                    )
                } else {
                    binding.root.background = null
                    binding.dayNumber.setTextColor(
                        binding.root.context.getColor(R.color.accent)
                    )
                }

                if (day.isToday) {
                    binding.dayNumber.setBackgroundResource(R.drawable.today_circle)
                    binding.dayNumber.setTextColor(
                        binding.root.context.getColor(R.color.main)
                    )
                } else {
                    binding.dayNumber.background = null
                }

                if (day.holidays.isNotEmpty()) {
                    val holiday = day.holidays.first()
                    binding.holidayIndicator.visibility = View.VISIBLE
                    binding.holidayIndicator.setBackgroundColor(
                        when (holiday.priority) {
                            0, 1 -> binding.root.context.getColor(R.color.holiday_great)
                            2 -> binding.root.context.getColor(R.color.holiday_middle)
                            else -> binding.root.context.getColor(R.color.holiday_small)
                        }
                    )
                    val size = (when (holiday.priority){
                        0, 1 -> 10
                        2 -> 8
                        else -> 6
                    } * Resources.getSystem().displayMetrics.density).toInt()
                    binding.holidayIndicator.layoutParams.height = size
                    binding.holidayIndicator.layoutParams.width = size
                } else {
                    binding.holidayIndicator.visibility = View.GONE
                }

                binding.fastIndicator.visibility = when (day.fastLevel) {
                    FastLevel.NO_FAST -> View.GONE
                    FastLevel.CONTINUOUS_WEEK -> View.GONE
                    else -> View.VISIBLE
                }

                binding.root.setOnClickListener {
                    val oldDate = selectedDate
                    onDateClick(day)
                    selectedDate = day
                    notifyItemChanged(adapterPosition)
                    if (oldDate != null) {
                        val oldPosition = currentList.indexOfFirst { it.date == oldDate.date }
                        if (oldPosition != -1) {
                            notifyItemChanged(oldPosition)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = CalendarDayItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
        override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem == newItem
        }
    }
}