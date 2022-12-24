package com.example.movieapp.ui.favorite

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.domain.model.Movie
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.databinding.FragmentFavoriteBinding
import com.example.movieapp.ui.MainActivity
import com.example.movieapp.ui.MainViewModel
import com.example.movieapp.ui.search.MovieAdapter
import com.example.movieapp.view.dialog.DialogUtil
import com.example.movieapp.view.listener.ItemTouchHelperCallback
import com.example.movieapp.view.listener.OnClickMovieListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_favorite

    val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var itemTouchHelper: ItemTouchHelper

    private val movieAdapter by lazy {
        MovieAdapter(requireContext(), object : OnClickMovieListener {
            override fun onClick(item: Movie, position: Int) {
                showSelectFavoriteDialog(item, position)
            }
        })
    }

    override fun initView() {
        (requireActivity() as MainActivity).setSupportActionBar(binding.favoriteTb)
        with(binding) {
            favoriteRv.setHasFixedSize(true)
            favoriteRv.adapter = movieAdapter
            itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(movieAdapter))
            itemTouchHelper.attachToRecyclerView(favoriteRv)
        }
        mainViewModel.getFavoriteMovieList()
    }

    override fun initEvent() {

    }

    override fun subscribe() {
        with(mainViewModel) {
            favoriteMovieListData.observe(viewLifecycleOwner) {
                movieAdapter.allClear()
                movieAdapter.addAll(it)
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

    private fun showSelectFavoriteDialog(movie: Movie, position: Int) {
        DialogUtil.makeSimpleDialog(
            context = requireContext(),
            title = getString(R.string.str_guide_delete_favorite),
            message = "",
            positiveButtonText = getString(R.string.str_delete_favorite),
            negativeButtonText = getString(R.string.str_cancel),
            positiveButtonOnClickListener = { dialog, i ->
                dialog.dismiss()
                mainViewModel.removeFavoriteMovie(movie, position, R.id.bottom_nav_favorite) {
                    movieAdapter.remove(movie)
                }
            },
            negativeButtonOnClickListener = { dialog, i ->
                dialog.dismiss()
            }
        ).show()

    }
    fun update() {
        movieAdapter.allClear()
//        movieAdapter.addAll(mainViewModel.favoriteMovieListData.value!!)
        mainViewModel.getFavoriteMovieList()
    }


    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragment()
        const val TAG = "FavoriteFragment"
    }
}