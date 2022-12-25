package com.example.movieapp.di

import android.content.Context
import androidx.room.Room
import com.example.data.source.local.AppDatabase
import com.example.data.source.local.dao.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideRoomDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "MovieApp.db")
            .allowMainThreadQueries()
            .build()
    }
    @Provides
    @Singleton
    fun provideVideoDao(appDatabase: AppDatabase): MovieDao {
        return appDatabase.movieDao()
    }
}