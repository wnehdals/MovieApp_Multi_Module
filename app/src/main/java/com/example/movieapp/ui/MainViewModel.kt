package com.example.movieapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.model.Movie
import com.example.domain.repository.MovieRepository
import com.example.movieapp.R
import com.example.movieapp.base.BaseViewModel
import com.example.movieapp.const.Const.API_KEY
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

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState


    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg


    var totalPage = 0
    val updateMovieIdList = mutableListOf<String>()    //즐겨찾기 탭에서 즐겨찾기 취소된 영화 id를 모은 리스트

    fun getFavoriteMovieList() {
        movieRepository
            .loadAllMovie()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _favoriteMovieListData.value!!.clear()
                _favoriteMovieListData.value = LinkedList(it)
            }, { throwable ->
                throwable.message?.let {
                    _errMsg.value = it
                }
            }).addTo(compositeDisposable)
    }

    fun getMovieList(keyword: String, page: Int) {
        movieRepository
            .getSearchResp(API_KEY, keyword, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                totalPage = getTotalPage(it.totalCnt)
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
            .doOnSubscribe { _loadingState.value = true }
            .subscribe({
                update(position)
                _loadingState.value = false
            }, { throwable ->
                throwable.message?.let {
                    _errMsg.value = it
                }
            }).addTo(compositeDisposable)
    }

    fun removeFavoriteMovie(movie: Movie, position: Int, tabId: Int, update: (Int) -> Unit) {
        movieRepository
            .deleteMovie(movie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _loadingState.value = true }
            .subscribe({
                if (tabId == R.id.bottom_nav_favorite) {
                    updateMovieIdList.add(movie.id)
                }
                update(position)
                _loadingState.value = false
            }, { throwable ->
                throwable.message?.let {
                    _errMsg.value = it
                }
            }).addTo(compositeDisposable)
    }

    fun changeFavoriteMovieRank(
        favoriteMovieList: LinkedList<Movie>,
        fromPosition: Int,
        toPosition: Int,
        onComplete: () -> Unit
    ) {
        var subList: MutableList<Movie> = mutableListOf()
        if (fromPosition > toPosition) {
            subList = favoriteMovieList.subList(toPosition, fromPosition + 1)
        } else {
            subList = favoriteMovieList.subList(fromPosition, toPosition + 1)
        }
        movieRepository.updateAllMovie(subList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onComplete()
            }, { throwable ->
                throwable.message?.let {
                    _errMsg.value = it
                }
            }).addTo(compositeDisposable)
    }

}