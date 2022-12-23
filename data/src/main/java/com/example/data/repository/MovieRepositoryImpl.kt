package com.example.data.repository

import com.example.data.mapper.toMovie
import com.example.data.mapper.toMovieEntity
import com.example.data.source.local.LocalMovieDataSource
import com.example.data.source.remote.RemoteMovieDataSource
import com.example.domain.model.Movie
import com.example.domain.model.SearchResp
import com.example.domain.repository.MovieRepository
import com.example.movieapp.data.entity.MovieEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val remoteMovieDataSource: RemoteMovieDataSource,
    private val localMovieDataSource: LocalMovieDataSource
) : MovieRepository {
    override fun getSearchResp(apiKey: String, keyword: String, page: Int): Single<SearchResp> {
        return Single.create { emitter ->
            remoteMovieDataSource.getSearchResp(apiKey, keyword, page)
                .subscribe({
                    if (it.response) {
                        emitter.onSuccess(it)
                    } else {
                        emitter.onSuccess(SearchResp.EMPTY)
                    }
                }, {
                    emitter.onError(Throwable(message = "network error"))
                })
        }
    }

    override fun insertMovie(movie: Movie): Completable {
        return localMovieDataSource.insert(movie.toMovieEntity())
    }

    override fun deleteMovie(movie: Movie): Completable {
        return localMovieDataSource.delete(movie.toMovieEntity())
    }

    override fun loadOneByMovieId(id_: Int): Single<Movie> {
        return localMovieDataSource.loadOneByMovieId(id_).map { it.toMovie() }
    }

    override fun loadAllMovie(): Single<MutableList<Movie>> {
        return localMovieDataSource.loadAll()
            .map { list ->
                list.asSequence()
                    .map { it.toMovie() }
                    .toMutableList()
            }
    }
}