package com.ecumeno.ui.reading

import android.R
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.text.Html
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ecumeno.data.local.database.DatabaseHelper
import com.ecumeno.data.local.preferences.PrefsHelper
import com.ecumeno.databinding.FragmentReaderBinding

class ReaderFragment : Fragment() {
    private var _binding: FragmentReaderBinding? = null
    private val binding get() = _binding!!
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var sensorManager: SensorManager
    private lateinit var prefs: PrefsHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: ReaderFragmentArgs by navArgs()
        val number = args.number
        val dbName = args.dbName
        val dbHelper = DatabaseHelper(requireContext(), dbName)
        prefs = PrefsHelper(requireContext())

        if (dbName.contains("bible")){
            val chapters = dbHelper.getChapters(number)
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_spinner_item,
                chapters.map { "${getString(com.ecumeno.R.string.chapter)} $it" })
            spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerChapters.adapter = spinnerAdapter


            binding.spinnerChapters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val verses = dbHelper.getVerses(number, chapters[position])
                    prefs.lastChapter = position
                    binding.textViewContent.text = verses.joinToString("\n\n") { "${it.verse}. ${Html.fromHtml(it.text.replace(Regex("<[Ss][^>]*>.*?</[Ss]>", RegexOption.IGNORE_CASE), ""))}".replace("\\[.*?\\]".toRegex(), "") }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            if (prefs.lastChapter != -1){
                binding.spinnerChapters.setSelection(prefs.lastChapter)
            }

            requireActivity().onBackPressedDispatcher.addCallback(this) {
                prefs.lastChapter = -1
                prefs.lastBook = -1
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        else {
            val prayers = dbHelper.getPrayers(number)
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_spinner_item,
                prayers.map { it.prayerName })
            spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerChapters.adapter = spinnerAdapter

            binding.spinnerChapters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val prayer = dbHelper.getPrayers(number)[position]
                    binding.textViewContent.text = prayer.text.replace("\\n", "\n\n")
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        binding.textViewContent.textSize = prefs.fontSize
        if (prefs.fontSize < 11f){
            binding.btnTextMinus.isEnabled = false
            binding.btnTextMinus.setAlpha(0.1f)
        }
        if (prefs.fontSize > 39f){
            binding.btnTextPlus.isEnabled = false
            binding.btnTextPlus.setAlpha(0.1f)
        }

        binding.btnTextMinus.setOnClickListener {
            if (prefs.fontSize > 10f) prefs.fontSize -= 2f
            binding.textViewContent.textSize = prefs.fontSize
            if (prefs.fontSize < 11f){
                binding.btnTextMinus.isEnabled = false
                binding.btnTextMinus.setAlpha(0.1f)
            }
            if (!binding.btnTextPlus.isEnabled){
                binding.btnTextPlus.isEnabled = true
                binding.btnTextPlus.setAlpha(1f)
            }
        }

        binding.btnTextPlus.setOnClickListener {
            if (prefs.fontSize < 40f) prefs.fontSize += 2f
            binding.textViewContent.textSize = prefs.fontSize
            if (prefs.fontSize > 39f){
                binding.btnTextPlus.isEnabled = false
                binding.btnTextPlus.setAlpha(0.1f)
            }
            if (!binding.btnTextMinus.isEnabled){
                binding.btnTextMinus.isEnabled = true
                binding.btnTextMinus.setAlpha(1f)
            }
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
                    val diffY = e2.y - (e1?.y ?: 0f)
                    if (Math.abs(diffX) > Math.abs(diffY) &&
                        Math.abs(diffX) > 100 &&
                        Math.abs(velocityX) > 100
                    ) {
                        if (diffX < 0 && binding.spinnerChapters.selectedItemPosition < binding.spinnerChapters.adapter.count - 1) {
                            binding.spinnerChapters.setSelection(binding.spinnerChapters.selectedItemPosition + 1)
                        } else if (binding.spinnerChapters.selectedItemPosition > 0 && diffX > 0) {
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