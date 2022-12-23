package com.example.data.source.remote

import com.example.domain.model.SearchResp
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/")
    fun getSearchResp(
        @Query("apikey") apiKey: String,
        @Query("s") keyword: String,
        @Query("page") page: Int
    ): Single<SearchResp>

}