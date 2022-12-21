package com.example.domain.repository

import com.example.domain.model.Movie
import com.example.domain.model.Search
import io.reactivex.rxjava3.core.Single

interface SearchRepository {
    fun getSearchResp(apiKey: String, keyword: String, page: Int): Single<Search>
}