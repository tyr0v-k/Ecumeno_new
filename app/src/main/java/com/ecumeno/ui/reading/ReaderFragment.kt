package com.ecumeno.ui.reading

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
import com.ecumeno.EcumenoApp
import com.ecumeno.data.local.database.DatabaseHelper
import com.ecumeno.data.local.preferences.Confession
import com.ecumeno.data.local.preferences.PreferencesRepository
import com.ecumeno.databinding.FragmentReaderBinding

class ReaderFragment : Fragment() {
    private var _binding: FragmentReaderBinding? = null
    private val binding get() = _binding!!
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var preferencesRepository: PreferencesRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: ReaderFragmentArgs by navArgs()
        val number = args.number
        val dbName = args.dbName
        preferencesRepository = (requireActivity().application as EcumenoApp).preferencesRepository
        val dbHelper = DatabaseHelper(
            requireContext(),
            dbName,
            Confession.Companion.fromPreferences(preferencesRepository.confession.value)
        )

        if (dbName.contains("bible")){
            val chapters = dbHelper.getChapters(number)
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                chapters.map { "${getString(com.ecumeno.R.string.chapter)} $it" })
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerChapters.adapter = spinnerAdapter


            binding.spinnerChapters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val verses = dbHelper.getVerses(number, chapters[position])
                    preferencesRepository.setLastChapter(position)
                    binding.textViewContent.text = verses.joinToString("\n\n") { "${it.verse}. ${Html.fromHtml(it.text.replace(Regex("<[Ss][^>]*>.*?</[Ss]>", RegexOption.IGNORE_CASE), ""))}".replace("\\[.*?\\]".toRegex(), "") }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            if (preferencesRepository.lastChapter.value != -1){
                binding.spinnerChapters.setSelection(preferencesRepository.lastChapter.value)
            }

            requireActivity().onBackPressedDispatcher.addCallback(this) {
                preferencesRepository.clearReadingProgress()
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        else {
            val prayers = dbHelper.getPrayers(number)
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                prayers.map { it.prayerName })
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

        dbHelper.close()

        binding.textViewContent.textSize = preferencesRepository.fontSize.value
        if (preferencesRepository.fontSize.value < 11f){
            binding.btnTextMinus.isEnabled = false
            binding.btnTextMinus.setAlpha(0.1f)
        }
        if (preferencesRepository.fontSize.value > 39f){
            binding.btnTextPlus.isEnabled = false
            binding.btnTextPlus.setAlpha(0.1f)
        }

        binding.btnTextMinus.setOnClickListener {
            if (preferencesRepository.fontSize.value > 10f) preferencesRepository.setFontSize(preferencesRepository.fontSize.value - 2f)
            binding.textViewContent.textSize = preferencesRepository.fontSize.value
            if (preferencesRepository.fontSize.value < 11f){
                binding.btnTextMinus.isEnabled = false
                binding.btnTextMinus.setAlpha(0.1f)
            }
            if (!binding.btnTextPlus.isEnabled){
                binding.btnTextPlus.isEnabled = true
                binding.btnTextPlus.setAlpha(1f)
            }
        }

        binding.btnTextPlus.setOnClickListener {
            if (preferencesRepository.fontSize.value < 40f) preferencesRepository.setFontSize(preferencesRepository.fontSize.value + 2f)
            binding.textViewContent.textSize = preferencesRepository.fontSize.value
            if (preferencesRepository.fontSize.value > 39f){
                binding.btnTextPlus.isEnabled = false
                binding.btnTextPlus.setAlpha(0.1f)
            }
            if (!binding.btnTextMinus.isEnabled){
                binding.btnTextMinus.isEnabled = true
                binding.btnTextMinus.setAlpha(1f)
            }
        }

        initGestureDetector()
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