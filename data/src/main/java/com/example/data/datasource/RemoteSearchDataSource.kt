package com.example.data.datasource

import com.example.data.api.ApiService
import com.example.movieapp.data.entity.SearchResp
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RemoteSearchDataSource @Inject constructor(private val apiService: ApiService) :
    SearchDataSource {
    override fun getSearchResp(apiKey: String, keyword: String, page: Int): Single<SearchResp> {
        return apiService.getSearchResp(apiKey, keyword, page)
    }
}