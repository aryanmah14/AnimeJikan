package com.example.animejikan.data.repository

import androidx.room.withTransaction
import com.example.animejikan.data.local.AnimeDatabase
import com.example.animejikan.data.local.AnimeEntity
import com.example.animejikan.data.remote.JikanApiService
import com.example.animejikan.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AnimeRepository @Inject constructor(
    private val api: JikanApiService,
    private val db: AnimeDatabase
) {
    private val dao = db.animeDao()

    fun getTopAnime(): Flow<Resource<List<AnimeEntity>>> = flow {
        emit(Resource.Loading())

        val localAnime = dao.getAllAnime()
        // We could emit local data first
        // But Flow from Room is tricky in this linear flow block unless we collect it.
        // Simplified approach: Fetch API -> Save DB -> Emit DB source
        
        try {
            val response = api.getTopAnime()
            val animeEntities = response.data.map { dto ->
                AnimeEntity(
                    malId = dto.malId,
                    title = dto.title,
                    imageUrl = dto.images.jpg.largeImageUrl ?: dto.images.jpg.imageUrl,
                    score = dto.score,
                    episodes = dto.episodes,
                    synopsis = dto.synopsis,
                    trailerUrl = dto.trailer?.embedUrl,
                    trailerImageUrl = dto.trailer?.images?.maximumImageUrl ?: dto.trailer?.images?.largeImageUrl ?: dto.trailer?.images?.mediumImageUrl ?: dto.trailer?.images?.imageUrl,
                    youTubeVideoId = dto.trailer?.youtubeId,
                    genres = dto.genres?.joinToString(", ") { it.name }
                )
            }
            
            db.withTransaction {
                dao.clearAll()
                dao.insertAll(animeEntities)
            }
            
        } catch (e: HttpException) {
            emit(Resource.Error("Server Error: ${e.code()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network Error. Showing cached data."))
        } catch (e: Exception) {
            emit(Resource.Error("Unknown Error: ${e.localizedMessage}"))
        }

        // Emit Source of Truth (DB)
        emitAll(
            dao.getAllAnime().map { 
                Resource.Success(it) 
            }
        )
    }
    
    // Better Pattern: separate trigger and observation or use networkBoundResource style.
    // Let's use a simpler approach for this "Simple App":
    // 1. Trigger network call (suspend function)
    // 2. Observe DB (Flow)
    
    suspend fun refreshTopAnime() {
        try {
            val response = api.getTopAnime()
            val animeEntities = response.data.map { dto ->
                AnimeEntity(
                    malId = dto.malId,
                    title = dto.title,
                    imageUrl = dto.images.jpg.largeImageUrl ?: dto.images.jpg.imageUrl,
                    score = dto.score,
                    episodes = dto.episodes,
                    synopsis = dto.synopsis,
                    trailerUrl = dto.trailer?.embedUrl,
                    trailerImageUrl = dto.trailer?.images?.maximumImageUrl ?: dto.trailer?.images?.largeImageUrl ?: dto.trailer?.images?.mediumImageUrl ?: dto.trailer?.images?.imageUrl,
                    youTubeVideoId = dto.trailer?.youtubeId,
                    genres = dto.genres?.joinToString(", ") { it.name }
                )
            }
            db.withTransaction {
                dao.clearAll()
                dao.insertAll(animeEntities)
            }
        } catch (e: Exception) {
            // Error handled by UI observing "Error" state or similar, 
            // In strict MVVM, Repository might define how to handle errors. 
            // Here, we'll throw or return Result. 
            throw e // Let ViewModel handle
        }
    }

    fun getAnimeListStream(): Flow<List<AnimeEntity>> = dao.getAllAnime()

    suspend fun getAnimeDetail(id: Int): Resource<AnimeEntity> {
        // Try to get from DB first
        val local = dao.getAnimeById(id)
        if (local != null) {
            // Optionally refresh in background? For detailed fields?
            // If local has enough info, return it. 
            // Current Entity has synopsis etc.
            return Resource.Success(local)
        }
        
        // If not in DB, fetch API
        return try {
            val response = api.getAnimeDetails(id)
            val dto = response.data
             val entity = AnimeEntity(
                    malId = dto.malId,
                    title = dto.title,
                    imageUrl = dto.images.jpg.largeImageUrl ?: dto.images.jpg.imageUrl,
                    score = dto.score,
                    episodes = dto.episodes,
                    synopsis = dto.synopsis,
                    trailerUrl = dto.trailer?.embedUrl,
                    trailerImageUrl = dto.trailer?.images?.maximumImageUrl ?: dto.trailer?.images?.largeImageUrl ?: dto.trailer?.images?.mediumImageUrl ?: dto.trailer?.images?.imageUrl,
                    youTubeVideoId = dto.trailer?.youtubeId,
                    genres = dto.genres?.joinToString(", ") { it.name }
                )
            // Save to DB?
            // dao.insertAll(listOf(entity)) 
            // Maybe don't clearAll here, just insert/update
            Resource.Success(entity)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error fetching details")
        }
    }
    suspend fun getAnimeCast(id: Int): Resource<List<com.example.animejikan.data.model.CharacterDto>> {
        return try {
            val response = api.getAnimeCharacters(id)
            Resource.Success(response.data)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error fetching cast")
        }
    }
}
