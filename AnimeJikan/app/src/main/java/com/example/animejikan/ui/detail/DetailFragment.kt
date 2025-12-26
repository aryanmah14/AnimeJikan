package com.example.animejikan.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.animejikan.databinding.FragmentDetailBinding
import com.example.animejikan.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        lifecycle.addObserver(binding.youtubePlayerView)

        val animeId = arguments?.getInt("animeId") ?: return

        viewModel.loadAnimeDetail(animeId)
        viewModel.loadCast(animeId)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.animeDetail.collect { resource ->
                        when(resource) {
                            is Resource.Loading -> binding.progressBarDetail.isVisible = true
                            is Resource.Success -> {
                                binding.progressBarDetail.isVisible = false
                                resource.data?.let { anime ->
                                    binding.tvDetailTitle.text = anime.title
                                    binding.tvDetailScore.text = "Score: ${anime.score ?: "N/A"}"
                                    binding.tvDetailEpisodes.text = "${anime.episodes ?: "?"} Episodes"
                                    binding.tvDetailGenres.text = anime.genres ?: "No Genres"
                                    binding.tvDetailSynopsis.text = anime.synopsis ?: "No Synopsis"
                                    
                                    val videoId = anime.youTubeVideoId ?: anime.trailerUrl?.let { url ->
                                         val regex = "(?:/embed/)([a-zA-Z0-9_-]{11})".toRegex()
                                         val match = regex.find(url)
                                         match?.groupValues?.get(1)
                                    }

                                    if (!videoId.isNullOrEmpty()) {
                                        binding.youtubePlayerView.isVisible = true
                                        binding.ivDetailPoster.isVisible = false
                                        binding.btnWatchOnYoutube.isVisible = false
                                        
                                        val options = com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions.Builder()
                                            .controls(1)
                                            .rel(0)
                                            .origin("https://www.youtube.com")
                                            .build()

                                        binding.youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
                                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                                youTubePlayer.loadVideo(videoId, 0f)
                                            }

                                            override fun onError(youTubePlayer: YouTubePlayer, error: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError) {
                                                super.onError(youTubePlayer, error)
                                                // Fallback to external player on error
                                                binding.youtubePlayerView.isVisible = false
                                                binding.ivDetailPoster.isVisible = true
                                                binding.btnWatchOnYoutube.isVisible = true
                                                
                                                val imageUrl = anime.trailerImageUrl ?: anime.imageUrl
                                                if (com.example.animejikan.util.AppConfig.SHOW_IMAGES) {
                                                    Glide.with(this@DetailFragment).load(imageUrl).into(binding.ivDetailPoster)
                                                }
                                            }
                                        }, options)

                                        binding.btnWatchOnYoutube.setOnClickListener {
                                            val intent = android.content.Intent(
                                                android.content.Intent.ACTION_VIEW, 
                                                android.net.Uri.parse("https://www.youtube.com/watch?v=$videoId")
                                            )
                                            startActivity(intent)
                                        }
                                    } else {
                                        binding.youtubePlayerView.isVisible = false
                                        binding.ivDetailPoster.isVisible = true
                                        binding.btnWatchOnYoutube.isVisible = false
                                        
                                        val imageUrl = anime.trailerImageUrl ?: anime.imageUrl
                                        if (com.example.animejikan.util.AppConfig.SHOW_IMAGES) {
                                            Glide.with(this@DetailFragment)
                                                .load(imageUrl)
                                                .into(binding.ivDetailPoster)
                                        } else {
                                            binding.ivDetailPoster.setBackgroundColor(android.graphics.Color.DKGRAY)
                                        }
                                    }
                                }
                            }
                            is Resource.Error -> {
                                binding.progressBarDetail.isVisible = false
                                // Handle error
                            }
                        }
                    }
                }
                launch {
                    viewModel.castList.collect { resource ->
                        if (resource is Resource.Success) {
                            val castText = resource.data?.joinToString("\n") { 
                                "${it.character.name} as ${it.role}" 
                            }
                            binding.tvDetailCast.text = castText ?: "No Cast Info"
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.youtubePlayerView.release()
        _binding = null
    }
}
