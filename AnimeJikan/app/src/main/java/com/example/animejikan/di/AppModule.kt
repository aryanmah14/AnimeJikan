package com.example.animejikan.di

import android.app.Application
import androidx.room.Room
import com.example.animejikan.data.local.AnimeDatabase
import com.example.animejikan.data.remote.JikanApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.jikan.moe/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideJikanApi(retrofit: Retrofit): JikanApiService {
        return retrofit.create(JikanApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AnimeDatabase {
        return Room.databaseBuilder(
            app,
            AnimeDatabase::class.java,
            "anime_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideAnimeDao(db: AnimeDatabase) = db.animeDao()
}
