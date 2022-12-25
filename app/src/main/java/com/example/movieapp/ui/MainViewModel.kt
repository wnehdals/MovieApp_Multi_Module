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
    /* 검색된 Movie 리스트 */
    private val _movieListData = MutableLiveData<MutableList<Movie>>(mutableListOf())
    val movieListData: LiveData<MutableList<Movie>> get() = _movieListData

    /* 즐겨찾기된 Movie 리스트 */
    private val _favoriteMovieListData = MutableLiveData<LinkedList<Movie>>(LinkedList())
    val favoriteMovieListData: LiveData<LinkedList<Movie>> get() = _favoriteMovieListData

    /* 로딩 상태를 나타내는 변수 */
    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    /* 오류 메시지르 나타내는 변수 */
    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg


    var totalPage = 0                                  // 검색 결과의 전체 페이지 수
    val updateMovieIdList = mutableListOf<String>()    // 즐겨찾기 탭에서 즐겨찾기 취소된 영화 id를 모은 리스트

    /**
     * totalPage, _movieListData를 초기화하는 함수
     */
    fun searchResultClear() {
        totalPage = 0
        _movieListData.value!!.clear()
    }
    /**
     * 즐겨찾기된 Movie들을 조회하는 함수
     */
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

    /**
     * 검색 결과를 조회하는 함수
     */
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

    /**
     * totalCnt로 전체 전체 페이지수를 구하는 함수
     * @param totalCnt 검색결과의 전체 수
     */
    fun getTotalPage(totalCnt: Int): Int {
        return if (totalCnt % 10 == 0) {
            (totalCnt / 10)
        } else {
            (totalCnt / 10) + 1
        }
    }

    /**
     * 즐겨찾기 항목에 추가하는 함수
     * @param movie 즐겨찾기에 추가할 Movie
     * @param position 즐겨찾기된 이후 갱신될 Movie의 position
     * @param update 즐겨찾기 or 검색 목록에서 Movie를 갱신하는 함수
     */
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


    /**
     * 즐겨찾기 항목에 삭제하는 함수
     * 즐겨찾기 탭에서 삭제하였다면 updateMovieIdList에 삭제한 Movie의id를 추가
     * 검색탭으로 이동 시 updateMovieIdList에 있는 id와 검색목록에 있는 Movie id를 비교해 검색목록 Movie를 갱신
     * @param movie 즐겨찾기에 삭제할 Movie
     * @param position 즐겨찾기된 이후 갱신될 Movie의 position
     * @param tabId 바텀네비게이션 탭 ID
     * @param update 즐겨찾기 or 검색 목록에서 Movie를 갱신하는 함수
     */
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

    /**
     * 즐겨찾기된 Movie의 순서를 변경하는 함수
     * @param favoriteMovieList 즐겨찾기 리스트
     * @param fromPosition Drag가 시작된 Position
     * @param toPosition Drop된 Position
     * @param onComplete Movie의 순서를 갱신하는 함수
     */
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