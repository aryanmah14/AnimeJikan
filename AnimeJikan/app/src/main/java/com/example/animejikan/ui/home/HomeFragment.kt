package com.example.animejikan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animejikan.R
import com.example.animejikan.databinding.FragmentHomeBinding
import com.example.animejikan.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var animeAdapter: AnimeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        animeAdapter = AnimeAdapter { anime ->
            // Navigate to Detail with Bundle
            val bundle = Bundle().apply {
                putInt("animeId", anime.malId)
            }
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }

        binding.rvAnime.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = animeAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.animeListState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.tvError.isVisible = false
                        }
                        is Resource.Success -> {
                            binding.progressBar.isVisible = false
                            binding.tvError.isVisible = false
                            animeAdapter.submitList(resource.data)
                        }
                        is Resource.Error -> {
                            binding.progressBar.isVisible = false
                            binding.tvError.isVisible = true
                            binding.tvError.text = resource.message
                            // Show data if available in error (offline mode)
                            if (resource.data?.isNotEmpty() == true) {
                                animeAdapter.submitList(resource.data)
                                Toast.makeText(context, "Offline Mode: ${resource.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
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
