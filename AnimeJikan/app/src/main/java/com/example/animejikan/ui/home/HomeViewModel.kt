package com.example.animejikan.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animejikan.data.local.AnimeEntity
import com.example.animejikan.data.repository.AnimeRepository
import com.example.animejikan.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _animeListState = MutableStateFlow<Resource<List<AnimeEntity>>>(Resource.Loading())
    val animeListState: StateFlow<Resource<List<AnimeEntity>>> = _animeListState

    init {
        fetchTopAnime()
    }

    fun fetchTopAnime() {
        viewModelScope.launch {
            repository.getTopAnime().collectLatest { result ->
                _animeListState.value = result
            }
        }
    }
}
