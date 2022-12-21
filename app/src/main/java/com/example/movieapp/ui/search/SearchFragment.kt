package com.example.movieapp.ui.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.inputmethod.EditorInfo
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.databinding.FragmentSearchBinding
import com.example.movieapp.ui.MainActivity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.movieapp.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_search
    val mainViewModel: MainViewModel by activityViewModels()
    override fun initView() {
        mainViewModel.getSearchData()
    }


    override fun initEvent() {
        binding.searchIv.setOnClickListener {
        }
    }

    override fun subscribe() {
    }


    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}