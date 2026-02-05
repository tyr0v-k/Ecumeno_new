package com.uvpv521.calendar.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uvpv521.calendar.R
import com.uvpv521.calendar.data.models.CalendarDay
import com.uvpv521.calendar.data.models.FastLevel
import com.uvpv521.calendar.databinding.CalendarDayItemBinding
import java.time.LocalDate

class CalendarAdapter(
    private val onDateClick: (LocalDate) -> Unit
) : ListAdapter<CalendarDay, CalendarAdapter.CalendarViewHolder>(DiffCallback()) {

    private var selectedDate: LocalDate? = null

    inner class CalendarViewHolder(
        private val binding: CalendarDayItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: CalendarDay) {
            binding.dayNumber.text = day.date.dayOfMonth.toString()
            binding.dayOfWeek.text = day.dayOfWeek

            // Выделение сегодняшнего дня
            if (day.isToday) {
                binding.dayNumber.setBackgroundResource(R.drawable.today_circle)
                binding.dayNumber.setTextColor(
                    binding.root.context.getColor(R.color.white)
                )
            } else {
                binding.dayNumber.background = null
                binding.dayNumber.setTextColor(
                    binding.root.context.getColor(R.color.black)
                )
            }

            if (day.date.month != currentList.get(20).date.month) {
                binding.dayNumber.setTextColor(
                    binding.root.context.getColor(R.color.grey)
                )
                binding.holidayIndicator.visibility = View.GONE
                binding.fastIndicator.visibility = View.GONE
            }else{
                // Выделение выбранного дня
                if (day.date == selectedDate) {
                    binding.root.setBackgroundResource(R.drawable.selected_day_bg)
                } else {
                    binding.root.background = null
                }

                // Отображение праздников
                if (day.holidays.isNotEmpty()) {
                    val holiday = day.holidays.first()
                    binding.holidayIndicator.visibility = View.VISIBLE
                    binding.holidayIndicator.setBackgroundColor(
                        when (holiday.priority) {
                            0, 1 -> binding.root.context.getColor(R.color.holiday_great)
                            2, 3 -> binding.root.context.getColor(R.color.holiday_middle)
                            else -> binding.root.context.getColor(R.color.holiday_small)
                        }
                    )
                } else {
                    binding.holidayIndicator.visibility = View.GONE
                }

                // Отображение поста
                binding.fastIndicator.visibility = when (day.fastLevel) {
                    FastLevel.NO_FAST -> View.GONE
                    FastLevel.CONTINUOUS_WEEK -> View.GONE
                    else -> View.VISIBLE
                }

                if(day.fastLevel == FastLevel.CONTINUOUS_WEEK){
                    binding.fastIndicator.setBackgroundColor(binding.root.context.getColor(R.color.holiday_middle))
                }

                binding.root.setOnClickListener {
                    onDateClick(day.date)
                    selectedDate = day.date
                    notifyDataSetChanged()
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

//    fun updateSelectedDate(date: LocalDate) {
//        selectedDate = date
//        notifyDataSetChanged()
//    }

    class DiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
        override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem == newItem
        }
    }
}