package com.example.movieapp.di

import com.example.data.datasource.RemoteSearchDataSource
import com.example.data.repository.SearchRepositoryImpl
import com.example.domain.repository.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideRepository(remoteDataSource: RemoteSearchDataSource): SearchRepository {
        return SearchRepositoryImpl(remoteDataSource)
    }
}