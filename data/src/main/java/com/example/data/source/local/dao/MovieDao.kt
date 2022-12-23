package com.example.data.source.local.dao

import androidx.room.*
import com.example.movieapp.data.entity.MovieEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movieEntity: MovieEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movieEntities: List<MovieEntity>): Completable

    @Update
    fun update(movieEntity: MovieEntity): Completable

    @Delete
    fun delete(movieEntity: MovieEntity): Completable

    @Query("SELECT * FROM MOVIE WHERE id = :id_" )
    fun loadOneByMovieId(id_: Int): Single<MovieEntity>

    @Query("SELECT * FROM MOVIE" )
    fun loadAll(): Single<List<MovieEntity>>
}