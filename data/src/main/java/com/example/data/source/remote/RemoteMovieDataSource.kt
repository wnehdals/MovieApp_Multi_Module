package com.example.data.source.remote

import com.example.domain.model.SearchResp
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RemoteMovieDataSource @Inject constructor(private val apiService: ApiService) {
    fun getSearchResp(apiKey: String, keyword: String, page: Int): Single<SearchResp> {
        return apiService.getSearchResp(apiKey, keyword, page)
    }
}