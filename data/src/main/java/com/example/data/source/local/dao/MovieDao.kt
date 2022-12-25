package com.example.data.source.local.dao

import androidx.room.*
import com.example.movieapp.data.entity.MovieEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movieEntity: MovieEntity): Completable

    @Update
    fun updateAll(movieEntities: List<MovieEntity>): Completable

    @Delete
    fun delete(movieEntity: MovieEntity): Completable

    @Query("SELECT * FROM MOVIE ORDER BY RANK ASC" )
    fun loadAll(): Single<List<MovieEntity>>
}