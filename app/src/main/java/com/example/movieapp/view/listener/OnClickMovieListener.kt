package com.example.movieapp.view.listener

import com.example.domain.model.Movie
import com.example.movieapp.view.listener.AdapterListener

interface OnClickMovieListener: AdapterListener {
    fun onClick(item: Movie, position: Int)
}