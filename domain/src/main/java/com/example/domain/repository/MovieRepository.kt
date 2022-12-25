package com.example.domain.repository

import com.example.domain.model.Movie
import com.example.domain.model.SearchResp
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface MovieRepository {
    fun getSearchResp(apiKey: String, keyword: String, page: Int): Single<SearchResp>

    fun insertMovie(movie: Movie): Completable

    fun updateAllMovie(movies: List<Movie>): Completable

    fun deleteMovie(movie: Movie): Completable

    fun loadAllMovie(): Single<MutableList<Movie>>
}