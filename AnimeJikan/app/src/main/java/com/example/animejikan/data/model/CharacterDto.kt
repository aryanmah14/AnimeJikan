package com.example.animejikan.data.model

import com.google.gson.annotations.SerializedName

data class CharactersResponse(
    @SerializedName("data") val data: List<CharacterDto>
)

data class CharacterDto(
    @SerializedName("character") val character: CharacterDataDto,
    @SerializedName("role") val role: String
)

data class CharacterDataDto(
    @SerializedName("name") val name: String,
    @SerializedName("images") val images: AnimeImagesDto // Reuse existing images DTO
)
