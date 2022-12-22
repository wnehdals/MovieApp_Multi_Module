package com.example.movieapp.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.movieapp.view.listener.AdapterListener

abstract class BaseViewHolder<T>(
    binding: ViewBinding,
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bindViews(item: T, position: Int, adapterListener: AdapterListener)

}