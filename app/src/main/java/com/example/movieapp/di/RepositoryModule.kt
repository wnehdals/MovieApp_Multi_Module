package com.example.movieapp.di

import com.example.data.source.remote.RemoteMovieDataSource
import com.example.data.repository.MovieRepositoryImpl
import com.example.data.source.local.LocalMovieDataSource
import com.example.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideMovieRepository(remoteMovieDataSource: RemoteMovieDataSource, localMovieDataSource: LocalMovieDataSource): MovieRepository {
        return MovieRepositoryImpl(remoteMovieDataSource, localMovieDataSource)
    }
}