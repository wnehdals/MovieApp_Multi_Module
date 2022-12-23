package com.example.movieapp.ui.search

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

class MovieAdapter(
    private val context: Context,
    private val adapterListener: AdapterListener
    ): RecyclerView.Adapter<BaseViewHolder<Movie>>(), ItemTouchHelperListener {

    val movieList = mutableListOf<Movie>()

    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Movie> {
        return when(viewType) {
            VIEW_TYPE_NORMAL -> NormalViewHolder(ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_LOADING -> LoadingViewHolder(ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> NormalViewHolder(ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Movie>, position: Int) {
        val item = movieList[position]
        holder.bindViews(item, position, adapterListener)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == movieList.size-1) VIEW_TYPE_LOADING
            else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val item = movieList.get(fromPosition)
        movieList.removeAt(fromPosition)
        movieList.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemSwipe(position: Int) {
    }

    fun addAll(data: MutableList<Movie>) {
        data.forEach { add(it) }
    }
    fun add(data: Movie) {
        movieList.add(data)
        notifyItemInserted(movieList.size-1)
    }
    fun remove(data: Movie) {
        var position = movieList.indexOf(data)

        if (position > -1) {
            movieList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    fun addLoading() {
        isLoaderVisible = true
        add(Movie("","","","",""))
    }
    fun getItem(position: Int): Movie? {
        if (position < 0)
            return null
        else if (position > movieList.size-1)
            return null
        else
            return movieList[position]
    }
    fun removeLoading() {
        isLoaderVisible = false
        var position = movieList.size-1
        var item = getItem(position)

        if (item != null) {
            movieList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    fun clear() {
        while (itemCount > 0) {
            getItem(0)?.let { remove(it) }
        }
    }
    fun allClear() {
        movieList.clear()
        notifyDataSetChanged()
    }

    fun updateItem(position: Int) {
        movieList[position].isFavorite = !movieList[position].isFavorite
        notifyItemChanged(position)
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
    inner class LoadingViewHolder(val binding: ItemLoadingBinding): BaseViewHolder<Movie>(binding) {
        override fun bindViews(item: Movie, position: Int, adapterListener: AdapterListener) {

        }
    }
}