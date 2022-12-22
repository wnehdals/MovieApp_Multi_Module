package com.example.movieapp.ui.search

import android.view.*
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.databinding.FragmentSearchBinding

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.domain.model.Movie
import com.example.movieapp.ui.MainViewModel
import com.example.movieapp.ui.SearchUi
import com.example.movieapp.view.dialog.DialogUtil
import com.example.movieapp.view.listener.ItemTouchHelperCallback
import com.example.movieapp.view.listener.OnClickMovieListener
import com.example.movieapp.view.listener.PaginationScrollListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(), SwipeRefreshLayout.OnRefreshListener {
    override val layoutId: Int
        get() = R.layout.fragment_search
    val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val PAGE_START = 1
    private var itemCount = 0
    private var currentPage = PAGE_START
    private var totalPage = 10

    private var isLastPage = false
    private var isLoading = false

    private val movieAdapter by lazy {
        MovieAdapter(requireContext(), object : OnClickMovieListener {
            override fun onClick(item: Movie, position: Int) {
                DialogUtil.makeSimpleDialog(
                    context=requireContext(),
                    title = getString(R.string.str_guide_add_favorite),
                    message="",
                    positiveButtonText = getString(R.string.str_favorite),
                    negativeButtonText = getString(R.string.str_cancel),
                    positiveButtonOnClickListener = { dialog, i ->
                        dialog.dismiss()
                    },
                    negativeButtonOnClickListener = { dialog, i ->
                        dialog.dismiss()
                    }
                ).show()
            }
        })
    }

    override fun initView() {
        with(binding) {
            searchSl.setOnRefreshListener(this@SearchFragment)
            searchRv.setHasFixedSize(true)
            val layoutManager = GridLayoutManager(requireContext(), 2)
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (movieAdapter.getItemViewType(position) == 0) 2
                    else 1
                }
            }
            searchRv.layoutManager = layoutManager
            searchRv.adapter = movieAdapter
            searchRv.addOnScrollListener(object : PaginationScrollListener(layoutManager, 10) {
                override fun isLoading(): Boolean {
                    return isLoading
                }

                override fun isLastPage(): Boolean {
                    return isLastPage
                }

                override fun loadMoreItems() {
                    isLoading = true
                    currentPage++
                    mainViewModel.getSearchData(searchEt.text.toString(), currentPage)
                }
            })
            itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(movieAdapter))
            itemTouchHelper.attachToRecyclerView(searchRv)
        }
    }


    override fun initEvent() {
        binding.searchIv.setOnClickListener {
            mainViewModel.getSearchData(binding.searchEt.text.toString(), PAGE_START)
        }
    }

    override fun subscribe() {
        with(mainViewModel) {
            searchUiState.observe(viewLifecycleOwner) {
                when (it) {
                    is SearchUi.Loading -> {

                    }
                    is SearchUi.Success -> {
                        val movies = mutableListOf<Movie>()
                        it.resp.totalCnt
                        movies.addAll(it.resp.movieList)

                        if (currentPage != PAGE_START) {
                            movieAdapter.removeLoading()
                        }
                        movieAdapter.addAll(movies)
                        binding.searchSl.isRefreshing = false
                        if (currentPage < totalPage) {
                            movieAdapter.addLoading()
                        } else {
                            isLastPage = true
                        }
                        isLoading = false

                    }
                    is SearchUi.Fail -> {

                    }
                }
            }
        }
    }

    override fun onRefresh() {
        itemCount = 0
        currentPage = PAGE_START
        isLastPage = false
        movieAdapter.clear()
        mainViewModel.getSearchData(binding.searchEt.text.toString(), PAGE_START)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}