package com.ecumeno.ui.reading.reader

import android.os.Bundle
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ecumeno.R
import com.ecumeno.databinding.FragmentReaderBinding
import kotlinx.coroutines.launch

class ReaderFragment : Fragment() {
    private var _binding: FragmentReaderBinding? = null
    private val binding get() = _binding!!
    private lateinit var gestureDetector: GestureDetectorCompat
    private val viewModel: ReaderViewModel by viewModels()
    private val args: ReaderFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()
        setupButtons()
        setupGestureDetector()
        setupBackPress()
        setupObservers()

        viewModel.initialize(args.number, args.dbName)
    }

    private fun setupSpinner() {
        binding.spinnerChapters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.onChapterSelected(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupButtons() {
        binding.btnTextMinus.setOnClickListener {
            viewModel.decreaseFontSize()
        }

        binding.btnTextPlus.setOnClickListener {
            viewModel.increaseFontSize()
        }
    }

    private fun setupGestureDetector() {
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
                        if (diffX < 0) {
                            viewModel.nextChapter()
                        } else {
                            viewModel.previousChapter()
                        }
                        return true
                    }
                    return false
                }

                override fun onDown(e: MotionEvent): Boolean = true
            }
        )

        view?.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        binding.textViewContent.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun setupBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.clearReadingProgress()
            isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.dataCleared){
                        findNavController().popBackStack()
                    }
                    if (binding.spinnerChapters.adapter == null ||
                        (binding.spinnerChapters.adapter as? ArrayAdapter<*>)?.count != state.chapters.size) {
                        val chapters = if (state.isBible) state.chapters.map { "${requireContext().getString(
                            R.string.chapter)} $it" } else state.chapters
                        val spinnerAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            chapters
                        )
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerChapters.adapter = spinnerAdapter
                    }

                    if (binding.spinnerChapters.selectedItemPosition != state.selectedChapterPosition) {
                        binding.spinnerChapters.setSelection(state.selectedChapterPosition)
                    }

                    if (binding.textViewContent.text.toString() != state.content) {
                        binding.textViewContent.text = state.content
                    }

                    binding.textViewContent.textSize = state.fontSize

                    binding.btnTextMinus.isEnabled = state.isMinusEnabled
                    binding.btnTextMinus.alpha = if (state.isMinusEnabled) 1f else 0.1f

                    binding.btnTextPlus.isEnabled = state.isPlusEnabled
                    binding.btnTextPlus.alpha = if (state.isPlusEnabled) 1f else 0.1f
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}