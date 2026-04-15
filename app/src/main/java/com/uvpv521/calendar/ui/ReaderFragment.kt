package com.uvpv521.calendar.ui

import android.content.Context
import android.hardware.SensorManager
import com.uvpv521.calendar.R
import android.os.Bundle
import android.os.Vibrator
import android.text.Html
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.uvpv521.calendar.data.database.DatabaseHelper
import com.uvpv521.calendar.databinding.FragmentReaderBinding
import kotlin.collections.joinToString
import kotlin.collections.map

class ReaderFragment : Fragment() {
    private var _binding: FragmentReaderBinding? = null
    private val binding get() = _binding!!
    private var currentFontSize = 18f
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var sensorManager: SensorManager



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: ReaderFragmentArgs by navArgs()
        val number = args.number
        val dbName = args.dbName
        val dbHelper = DatabaseHelper(requireContext(), dbName)

        if (dbName.contains("bible")){
            // Настройка спиннера глав
            val chapters = dbHelper.getChapters(number)
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, chapters.map { "${getString(R.string.chapter)} $it" })
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerChapters.adapter = spinnerAdapter

            binding.spinnerChapters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val verses = dbHelper.getVerses(number, chapters[position])
                    // Собираем все стихи в один текст для простоты
                    binding.textViewContent.text = verses.joinToString("\n\n") { "${it.verse}. ${Html.fromHtml(it.text.replace(Regex("<[Ss][^>]*>.*?</[Ss]>", RegexOption.IGNORE_CASE), ""))}".replace("\\[.*?\\]".toRegex(), "") }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        else {
            val prayers = dbHelper.getPrayers(number)
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, prayers.map { it.prayerName })
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerChapters.adapter = spinnerAdapter

            binding.spinnerChapters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val prayer = dbHelper.getPrayers(number)[position]
                    binding.textViewContent.text = prayer.text.replace("\\n", "\n\n")
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // Настройка размера шрифта
        binding.textViewContent.textSize = currentFontSize

        binding.btnTextMinus.setOnClickListener {
            if (currentFontSize > 10f) currentFontSize -= 2f
            binding.textViewContent.textSize = currentFontSize
        }

        binding.btnTextPlus.setOnClickListener {
            if (currentFontSize < 40f) currentFontSize += 2f
            binding.textViewContent.textSize = currentFontSize
        }

        initGestureDetector()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
        binding.textViewContent.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
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
                val diffY = e2.y - (e1?.y ?: 0f)
                if (Math.abs(diffX) > Math.abs(diffY) &&
                    Math.abs(diffX) > 100 &&
                    Math.abs(velocityX) > 100) {
                    if (diffX < 0 && binding.spinnerChapters.selectedItemPosition < binding.spinnerChapters.adapter.count - 1) {
                        binding.spinnerChapters.setSelection(binding.spinnerChapters.selectedItemPosition + 1)
                    } else if (binding.spinnerChapters.selectedItemPosition > 0 && diffX > 0){
                        binding.spinnerChapters.setSelection(binding.spinnerChapters.selectedItemPosition - 1)
                    }
                    return true
                }
                return false
            }

            override fun onDown(e: MotionEvent): Boolean = true
        })
    }
}