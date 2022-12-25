package com.example.movieapp.ui.search

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.domain.model.Movie
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.databinding.FragmentSearchBinding
import com.example.movieapp.ui.MainActivity
import com.example.movieapp.ui.MainViewModel
import com.example.movieapp.view.dialog.DialogUtil
import com.example.movieapp.view.listener.OnClickMovieListener
import com.example.movieapp.view.listener.PaginationScrollListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(), SwipeRefreshLayout.OnRefreshListener {
    override val layoutId: Int
        get() = R.layout.fragment_search
    val mainViewModel: MainViewModel by activityViewModels()
    private val PAGE_START = 1
    private var currentPage = PAGE_START

    private var isLastPage = false
    private var isLoading = false

    private val movieAdapter by lazy {
        MovieAdapter(requireContext(), object : OnClickMovieListener {
            override fun onClick(item: Movie, position: Int) {
                showSelectFavoriteDialog(item, position)
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackPressedCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2000) {
                    requireActivity().finish()
                } else {
                    showBackpressedToastMessage()
                    backPressedTime = System.currentTimeMillis()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallBack!!)
    }

    override fun initView() {
        with(binding) {
            (requireActivity() as MainActivity).setSupportActionBar(binding.searchTb)
            searchSl.setOnRefreshListener(this@SearchFragment)
            searchRv.setHasFixedSize(true)
            val layoutManager = GridLayoutManager(requireContext(), 2)
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (movieAdapter.getItemViewType(position) == 0) 2   // Movie 정보를 보여줄 경우 spancount를 2로 설정
                    else 1                                                      // 로딩일 경우 spancount를 1로 설정
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
                    mainViewModel.getMovieList(searchEt.text.toString(), currentPage)
                }
            })
        }
    }


    override fun initEvent() {
        with(binding) {
            searchIv.setOnClickListener {
                onRefresh()
                hideKeyboard()
            }
            searchEt.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onRefresh()
                    hideKeyboard()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }


    }

    override fun subscribe() {
        with(mainViewModel) {
            movieListData.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    setSearchResultVisible(true)
                    binding.searchSl.isRefreshing = false
                } else {
                    setSearchResultVisible(false)

                    if (currentPage != PAGE_START) {
                        movieAdapter.removeLoading()
                    }
                    movieAdapter.addAll(it)
                    binding.searchSl.isRefreshing = false
                    if (currentPage < mainViewModel.totalPage) {
                        movieAdapter.addLoading()
                    } else {
                        isLastPage = true
                    }
                    isLoading = false
                }
            }
            loadingState.observe(viewLifecycleOwner) {
                if (it)
                    showProgressDialog()
                else
                    dismissProgressDialog()
            }
            errMsg.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 검색 목록을 갱신하는 함수
     */
    override fun onRefresh() {
        currentPage = PAGE_START
        isLastPage = false
        movieAdapter.clear()
        mainViewModel.searchResultClear()
        mainViewModel.getMovieList(binding.searchEt.text.toString(), PAGE_START)
    }

    /**
     * 즐겨찾기 탭에서 삭제된 Movie가 있고 해당 Movie가 검색 목록에 있다면
     * 즐겨찾기 여부를 보여주는 UI를 변경하는 함수
     */
    fun update() {
        mainViewModel.updateMovieIdList.forEach { id ->
            movieAdapter.getMovieList().forEachIndexed { index, movie ->
                if (id == movie.id) {
                    movieAdapter.updateItem(index)
                }
            }
        }
        mainViewModel.updateMovieIdList.clear()
    }

    /**
     * @param isEmpty 검색결과가 비었는지 여부를 의미하는 변수
     */
    fun setSearchResultVisible(isEmpty: Boolean) {
        if (isEmpty) {
            binding.searchRv.visibility = View.GONE
            binding.searchEmptyResultTv.visibility = View.VISIBLE
        } else {
            binding.searchRv.visibility = View.VISIBLE
            binding.searchEmptyResultTv.visibility = View.GONE
        }
    }

    /**
     * 즐겨찾기 추가/제거 여부를 선택하는 다이얼로그를 보여주는 함수
     * @param movie 검색목록에서 선택한 Movie
     * @param position 검색목록에서 선택한 Moviedml position
     */
    private fun showSelectFavoriteDialog(movie: Movie, position: Int) {
        if (movie.isFavorite) {                                             // 즐겨찾기가 되어있는 경우
            DialogUtil.makeSimpleDialog(
                context = requireContext(),
                title = getString(R.string.str_guide_delete_favorite),
                message = "",
                positiveButtonText = getString(R.string.str_delete_favorite),
                negativeButtonText = getString(R.string.str_cancel),
                positiveButtonOnClickListener = { dialog, i ->
                    dialog.dismiss()
                    mainViewModel.removeFavoriteMovie(movie, position, R.id.bottom_nav_search) {
                        movieAdapter.updateItem(it)
                    }
                },
                negativeButtonOnClickListener = { dialog, i ->
                    dialog.dismiss()
                }
            ).show()
        } else {                                                            // 즐겨찾기가 되어있지 않은 경우
            DialogUtil.makeSimpleDialog(
                context = requireContext(),
                title = getString(R.string.str_guide_add_favorite),
                message = "",
                positiveButtonText = getString(R.string.str_favorite),
                negativeButtonText = getString(R.string.str_cancel),
                positiveButtonOnClickListener = { dialog, i ->
                    dialog.dismiss()
                    mainViewModel.addFavoriteMovie(movie, position) {
                        movieAdapter.updateItem(position)
                    }
                },
                negativeButtonOnClickListener = { dialog, i ->
                    dialog.dismiss()
                }
            ).show()
        }
    }
    /**
     * onBackPressedCallBack을 설정하는 함수
     */
    fun setDispatcher() {
        onBackPressedCallBack?.let {
            requireActivity().onBackPressedDispatcher.addCallback(this, it)
        }
    }

    override fun onDestroy() {
        onBackPressedCallBack?.remove()
        onBackPressedCallBack = null
        super.onDestroy()
    }
    /**
     * keyboard를 숨기는 함수
     */
    fun hideKeyboard() {
        val imm = (requireActivity() as MainActivity).getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEt.windowToken, 0)
    }
    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
        const val TAG = "SearchFragment"
    }
}