package com.example.movieapp.di

import com.example.data.source.remote.ApiService
import com.example.data.source.local.dao.MovieDao
import com.example.data.source.local.LocalMovieDataSource
import com.example.data.source.remote.RemoteMovieDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideRemoteMovieDataSource(service: ApiService): RemoteMovieDataSource {
        return RemoteMovieDataSource(service)
    }
    @Provides
    @Singleton
    fun provideLocalMovieDataSource(movieDao: MovieDao): LocalMovieDataSource {
        return LocalMovieDataSource(movieDao)
    }
}