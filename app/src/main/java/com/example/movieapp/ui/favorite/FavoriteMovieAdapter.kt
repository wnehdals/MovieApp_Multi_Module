package com.example.movieapp.ui.favorite

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.domain.model.Movie
import com.example.movieapp.R
import com.example.movieapp.base.BaseViewHolder
import com.example.movieapp.databinding.ItemLoadingBinding
import com.example.movieapp.databinding.ItemMovieBinding
import com.example.movieapp.view.listener.AdapterListener
import com.example.movieapp.view.listener.ItemTouchHelperListener
import com.example.movieapp.view.listener.OnClickMovieListener
import java.util.LinkedList

class FavoriteMovieAdapter(
    private val context: Context,
    private val adapterListener: AdapterListener
    ): RecyclerView.Adapter<BaseViewHolder<Movie>>() {

    private val movieList = LinkedList<Movie>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Movie> {
        return NormalViewHolder(ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Movie>, position: Int) {
        val item = movieList[position]
        holder.bindViews(item, position, adapterListener)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }
    fun addAll(data: LinkedList<Movie>) {
        movieList.addAll(data)
        notifyDataSetChanged()
    }
    fun allClear() {
        movieList.clear()
        notifyDataSetChanged()
    }

    fun getMovieList(): LinkedList<Movie> {
        return movieList
    }
    fun remove(data: Movie) {
        var position = movieList.indexOf(data)
        if (position > -1) {
            movieList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    inner class NormalViewHolder(val binding: ItemMovieBinding): BaseViewHolder<Movie>(binding) {
        override fun bindViews(item: Movie, position: Int, adapterListener: AdapterListener) {
            with(binding) {
                Glide.with(context)
                    .load(item.poster)
                    .placeholder(R.drawable.ic_warning_24_acb5bd)
                    .error(R.drawable.ic_warning_24_acb5bd)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(moviePosterIv)

                movieTitleTv.text = item.title
                movieYearTv.text = item.year
                movieTypeTv.text = item.type
                movieFavoriteIv.isSelected = item.isFavorite
                movieCl.setOnClickListener {
                    if (adapterListener is OnClickMovieListener) {
                        adapterListener.onClick(item, position)
                    }
                }
            }
        }

    }
}