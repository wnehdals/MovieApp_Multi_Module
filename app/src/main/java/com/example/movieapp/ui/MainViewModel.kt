package com.example.movieapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.model.Movie
import com.example.domain.repository.MovieRepository
import com.example.movieapp.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val movieRepository: MovieRepository) :
    BaseViewModel() {
    private val _movieListData = MutableLiveData<MutableList<Movie>>(mutableListOf())
    val movieListData: LiveData<MutableList<Movie>> get() = _movieListData

    private val _favoriteMovieListData = MutableLiveData<LinkedList<Movie>>(LinkedList())
    val favoriteMovieListData: LiveData<LinkedList<Movie>> get() = _favoriteMovieListData


    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg


    var totalPage = 0

    fun getFavoriteMovieList() {
        movieRepository
            .loadAllMovie()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _favoriteMovieListData.value = LinkedList(it)
            }, { throwable ->
                throwable.message?.let {
                    _errMsg.value = it
                }
            }).addTo(compositeDisposable)
    }
    fun getMovieList(keyword: String, page: Int) {
        movieRepository
            .getSearchResp("92e32667", keyword, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                totalPage = getTotalPage(it.totalCnt)
                for(movie in it.searches) {
                    for(favorite in _favoriteMovieListData.value!!) {
                        if (movie.id == favorite.id) {
                            movie.isFavorite = true
                            break
                        }
                    }
                }
                _movieListData.value = it.searches
            }, { throwable ->
                throwable.message?.let {
                    _errMsg.value = it
                }

            }).addTo(compositeDisposable)
    }
    fun getTotalPage(totalCnt: Int): Int {
        return if (totalCnt % 10 == 0) {
            (totalCnt / 10)
        } else {
            (totalCnt / 10) + 1
        }
    }
    fun addFavoriteMovie(movie: Movie, position: Int, update: (Int) -> Unit) {
        movieRepository
            .insertMovie(movie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _favoriteMovieListData.value!!.add(movie)
                update(position)
            }, { throwable ->
                throwable.message?.let {
                    _errMsg.value = it
                }
            }).addTo(compositeDisposable)
    }
    fun removeFavoriteMovie(movie: Movie, position: Int, update: (Int) -> Unit) {
        movieRepository
            .deleteMovie(movie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var idx = 0
                for(i in _favoriteMovieListData.value!!.indices) {
                    if (_favoriteMovieListData.value!![i].id == movie.id) {
                        idx = i
                        break
                    }
                }
                _favoriteMovieListData.value!!.removeAt(idx)
                update(position)
            }, { throwable ->
                throwable.message?.let {
                    _errMsg.value = it
                }
            }).addTo(compositeDisposable)
    }

}