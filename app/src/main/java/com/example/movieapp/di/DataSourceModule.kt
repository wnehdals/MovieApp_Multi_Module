package com.example.movieapp.di

import com.example.data.api.ApiService
import com.example.data.datasource.RemoteSearchDataSource
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
    fun provideRemoteSearchDataSource(service: ApiService): RemoteSearchDataSource {
        return RemoteSearchDataSource(service)
    }

}