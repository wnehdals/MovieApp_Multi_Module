package com.example.domain.repository

import com.example.domain.model.Movie
import com.example.domain.model.SearchResp
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import java.util.LinkedList

interface MovieRepository {
    fun getSearchResp(apiKey: String, keyword: String, page: Int): Single<SearchResp>

    fun insertMovie(movie: Movie): Completable

    fun deleteMovie(movie: Movie): Completable

    fun loadOneByMovieId(id_: Int): Single<Movie>

    fun loadAllMovie(): Single<MutableList<Movie>>
}