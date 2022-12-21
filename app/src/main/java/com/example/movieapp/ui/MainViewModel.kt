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
    private val _searchUIState = MutableLiveData<SearchUI>(SearchUI.Uninitialized)
    val searchUIState: LiveData<SearchUI> get() = _searchUIState

    fun getSearchData() {
        searchRepository
            .getSearchResp("92e32667", "iron man", 1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _searchUIState.value = SearchUI.Loading }
            .subscribe({
                _searchUIState.value = SearchUI.Success(it)
            }, { throwable ->
                throwable.message?.let {
                    _searchUIState.value = SearchUI.Fail(it)
                }

            })
    }

}

sealed class SearchUI {
    object Uninitialized : SearchUI()
    object Loading : SearchUI()
    data class Success(
        val resp: Search
    ) : SearchUI()

    data class Fail(
        val resp: String
    ) : SearchUI()
}