package com.example.data.source.local

import com.example.data.source.local.dao.MovieDao
import com.example.movieapp.data.entity.MovieEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LocalMovieDataSource @Inject constructor(
    private val movieDao: MovieDao
){
    fun insert(movieEntity: MovieEntity): Completable {
        return movieDao.insert(movieEntity)
    }

    fun insertAll(movieEntities: List<MovieEntity>): Completable {
        return movieDao.insertAll(movieEntities)
    }

    fun update(movieEntity: MovieEntity): Completable {
        return movieDao.update(movieEntity)
    }

    fun delete(movieEntity: MovieEntity): Completable {
        return movieDao.delete(movieEntity)
    }

    fun loadOneByMovieId(id_: Int): Single<MovieEntity> {
        return movieDao.loadOneByMovieId(id_)
    }

    fun loadAll(): Single<List<MovieEntity>> {
        return movieDao.loadAll()
    }
}