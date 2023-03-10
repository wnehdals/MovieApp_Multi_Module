package com.example.data.repository

import com.example.data.mapper.toMovie
import com.example.data.mapper.toMovieEntity
import com.example.data.source.local.LocalMovieDataSource
import com.example.data.source.remote.RemoteMovieDataSource
import com.example.domain.model.Movie
import com.example.domain.model.SearchResp
import com.example.domain.repository.MovieRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val remoteMovieDataSource: RemoteMovieDataSource,
    private val localMovieDataSource: LocalMovieDataSource
) : MovieRepository {
    /**
     * 검색결과의 Movie와 즐겨찾기된 Movie의 ID를 비교하여
     * 검색결과의 Movie의 즐겨찾기 여부를 설정한 후 검색결과를 반환하는 함수
     * @param apiKey API KEY
     * @param keyword 검색 keyword
     * @param page page 번호
     */
    override fun getSearchResp(apiKey: String, keyword: String, page: Int): Single<SearchResp> {
        return Single.create { emitter ->
            remoteMovieDataSource.getSearchResp(apiKey, keyword, page)
                .zipWith(loadAllMovie()) { searchResp, favorites ->
                    if (searchResp.response) {
                        for (movie in searchResp.searches) {
                            for (favorite in favorites) {
                                if (movie.id == favorite.id) {
                                    movie.isFavorite = true
                                    break
                                }
                            }
                        }
                        searchResp
                    } else {
                        SearchResp.EMPTY
                    }
                }
                .subscribe({
                    emitter.onSuccess(it)
                }, {
                    emitter.onError(Throwable(message = "network error"))
                })
        }
    }

    /**
     * Movie의 순서로 설정한 후 즐겨찾기 항목에 추가하는 함수
     * @param movie 즐겨찾기 항목에 추가될 Movie
     */
    override fun insertMovie(movie: Movie): Completable {
        return localMovieDataSource.loadAll()
            .map { it.size }
            .flatMapCompletable {
                movie.rank = it
                localMovieDataSource.insert(movie.toMovieEntity())
            }
    }

    /**
     * 즐겨찾기 항목에서 Movie를 삭제한 후
     * 즐겨찾기 항목의 순서를 갱신하는 함수
     * @param movie 즐겨찾기 항목에 삭제될 Movie
     */
    override fun deleteMovie(movie: Movie): Completable {
        return localMovieDataSource.delete(movie.toMovieEntity())
            .andThen(loadAllMovie())
            .map { list ->
                list.forEachIndexed { index, movie ->
                    movie.rank = index
                }
                list
            }.flatMapCompletable {
                updateAllMovie(it)
            }


    }

    /**
     * 즐겨찾기 항목을 반환하는 함수
     */
    override fun loadAllMovie(): Single<MutableList<Movie>> {
        return localMovieDataSource.loadAll()
            .map { list ->
                list.asSequence()
                    .map { it.toMovie() }
                    .toMutableList()
            }
    }

    /**
     * 즐겨찾기 항목을 갱신하는 함수
     * @param movies 갱신할 즐겨찾기 리스트
     */
    override fun updateAllMovie(movies: List<Movie>): Completable {
        val movies = movies.asSequence().map { it.toMovieEntity() }.toMutableList()
        return localMovieDataSource.updateAll(movies)
    }
}