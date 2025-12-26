package com.example.animejikan.data.remote

import com.example.animejikan.data.model.AnimeDetailResponse
import com.example.animejikan.data.model.TopAnimeResponse
import com.example.animejikan.data.model.CharactersResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface JikanApiService {

    @GET("top/anime")
    suspend fun getTopAnime(): TopAnimeResponse

    @GET("anime/{id}")
    suspend fun getAnimeDetails(@Path("id") id: Int): AnimeDetailResponse

    @GET("anime/{id}/characters")
    suspend fun getAnimeCharacters(@Path("id") id: Int): CharactersResponse
}

