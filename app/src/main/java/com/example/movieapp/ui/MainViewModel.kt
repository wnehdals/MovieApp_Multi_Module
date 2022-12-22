package com.example.movieapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.model.Search
import com.example.domain.repository.SearchRepository
import com.example.movieapp.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val searchRepository: SearchRepository) :
    BaseViewModel() {
    private val _searchUiState = MutableLiveData<SearchUi>(SearchUi.Uninitialized)
    val searchUiState: LiveData<SearchUi> get() = _searchUiState

    fun getSearchData(keyword: String, page: Int) {
        searchRepository
            .getSearchResp("92e32667", keyword, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _searchUiState.value = SearchUi.Loading }
            .subscribe({
                _searchUiState.value = SearchUi.Success(it)
            }, { throwable ->
                throwable.message?.let {
                    _searchUiState.value = SearchUi.Fail(it)
                }

            })
    }

}

sealed class SearchUi {
    object Uninitialized : SearchUi()
    object Loading : SearchUi()
    data class Success(
        val resp: Search
    ) : SearchUi()

    data class Fail(
        val resp: String
    ) : SearchUi()
}