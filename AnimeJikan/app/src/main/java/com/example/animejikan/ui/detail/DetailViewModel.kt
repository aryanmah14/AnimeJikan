package com.example.animejikan.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animejikan.data.local.AnimeEntity
import com.example.animejikan.data.model.CharacterDto
import com.example.animejikan.data.repository.AnimeRepository
import com.example.animejikan.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _animeDetail = MutableStateFlow<Resource<AnimeEntity>>(Resource.Loading())
    val animeDetail: StateFlow<Resource<AnimeEntity>> = _animeDetail

    private val _castList = MutableStateFlow<Resource<List<CharacterDto>>>(Resource.Loading())
    val castList: StateFlow<Resource<List<CharacterDto>>> = _castList

    fun loadAnimeDetail(id: Int) {
        viewModelScope.launch {
            _animeDetail.value = repository.getAnimeDetail(id)
        }
    }
    
    fun loadCast(id: Int) {
        viewModelScope.launch {
            try {
                val result = repository.getAnimeCast(id)
                _castList.value = result
            } catch (e: Exception) {
                _castList.value = Resource.Error(e.localizedMessage ?: "Error loading cast")
            }
        }
    }
}
