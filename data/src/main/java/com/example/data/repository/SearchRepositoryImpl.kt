package com.example.data.repository

import com.example.data.datasource.RemoteSearchDataSource
import com.example.data.mapper.toEmptySearch
import com.example.data.mapper.toSearch
import com.example.domain.model.Search
import com.example.domain.repository.SearchRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val remoteSearchDataSource: RemoteSearchDataSource
) : SearchRepository {
    override fun getSearchResp(apiKey: String, keyword: String, page: Int): Single<Search> {
        return Single.create { emitter ->
            remoteSearchDataSource.getSearchResp(apiKey, keyword, page)
                .subscribe({
                    if (it.response) {
                        val search = it.toSearch()
                        search.curPage = page
                        emitter.onSuccess(search)
                    } else {
                        emitter.onSuccess(it.toEmptySearch())
                    }
                }, {
                    emitter.onError(Throwable(message = "network error"))
                })
        }
    }
}