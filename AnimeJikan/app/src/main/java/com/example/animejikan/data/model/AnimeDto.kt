package com.example.animejikan.data.model

import com.google.gson.annotations.SerializedName

data class TopAnimeResponse(
    @SerializedName("data") val data: List<AnimeDto>
)

data class AnimeDto(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("images") val images: AnimeImagesDto,
    @SerializedName("score") val score: Double?,
    @SerializedName("episodes") val episodes: Int?,
    @SerializedName("synopsis") val synopsis: String?,
    @SerializedName("genres") val genres: List<GenreDto>?,
    @SerializedName("trailer") val trailer: TrailerDto?
)

data class AnimeImagesDto(
    @SerializedName("jpg") val jpg: ImageUrlDto
)

data class ImageUrlDto(
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("large_image_url") val largeImageUrl: String?
)

data class GenreDto(
    @SerializedName("name") val name: String
)

data class TrailerDto(
    @SerializedName("youtube_id") val youtubeId: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("embed_url") val embedUrl: String?,
    @SerializedName("images") val images: TrailerImagesDto?
)

data class TrailerImagesDto(
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("small_image_url") val smallImageUrl: String?,
    @SerializedName("medium_image_url") val mediumImageUrl: String?,
    @SerializedName("large_image_url") val largeImageUrl: String?,
    @SerializedName("maximum_image_url") val maximumImageUrl: String?
)

data class AnimeDetailResponse(
    @SerializedName("data") val data: AnimeDto
)
