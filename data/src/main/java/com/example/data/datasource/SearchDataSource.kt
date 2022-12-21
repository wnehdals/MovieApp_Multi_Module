package com.example.data.datasource

import com.example.movieapp.data.entity.SearchResp
import io.reactivex.rxjava3.core.Single

interface SearchDataSource {
    fun getSearchResp(apiKey: String, keyword: String, page: Int): Single<SearchResp>
}