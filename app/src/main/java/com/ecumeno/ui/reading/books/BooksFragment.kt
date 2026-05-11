package com.ecumeno.ui.reading.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ecumeno.EcumenoApp
import com.ecumeno.databinding.FragmentBooksBinding
import kotlinx.coroutines.launch

class BooksFragment : Fragment() {
    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!
    private val args: BooksFragmentArgs by navArgs()
    private lateinit var viewModel: BooksViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupObservers()
    }

    private fun setupViewModel() {
        val application = requireActivity().application as EcumenoApp
        val preferencesRepository = application.preferencesRepository
        viewModel = ViewModelProvider(
            this,
            BooksViewModelFactory(
                application,
                preferencesRepository,
                args.dbName
            )
        )[BooksViewModel::class.java]
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.itemNumber != -1){
                        val action = BooksFragmentDirections.actionBooksToReader(state.dbName, state.itemNumber)
                        viewModel.onNavigationHandled()
                        findNavController().navigate(action)
                    }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        state.items
                    )
                    binding.listViewBooks.adapter = adapter
                    binding.listViewBooks.setOnItemClickListener { _, _, position, _ ->
                        viewModel.onItemClicked(position)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}