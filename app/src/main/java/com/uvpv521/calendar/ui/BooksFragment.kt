package com.uvpv521.calendar.ui

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uvpv521.calendar.data.database.DatabaseHelper
import com.uvpv521.calendar.databinding.FragmentBooksBinding
import kotlin.collections.map

class BooksFragment : Fragment() {
    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!

    private val args: BooksFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dbName = args.dbName
        val dbHelper = DatabaseHelper(requireContext(), dbName)
        if (dbName.contains("bible")){
            val books = dbHelper.getBooks()

            val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, books.map { it.longName })
            binding.listViewBooks.adapter = adapter

            binding.listViewBooks.setOnItemClickListener { _, _, position, _ ->
                val bookNumber = books[position].bookNumber
                val action = BooksFragmentDirections.actionBooksToReader(args.dbName, bookNumber)
                findNavController().navigate(action)
            }
        }
        else{
            val categories = dbHelper.getCategories()

            val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, categories.map { it.name })
            binding.listViewBooks.adapter = adapter

            binding.listViewBooks.setOnItemClickListener { _, _, position, _ ->
                val categoryNumber = categories[position].categoryNumber
                val action = BooksFragmentDirections.actionBooksToReader(args.dbName, categoryNumber)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}