package com.example.movieapp.ui.favorite

import android.content.Context
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.domain.model.Movie
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.databinding.FragmentFavoriteBinding
import com.example.movieapp.ui.MainActivity
import com.example.movieapp.ui.MainViewModel
import com.example.movieapp.view.dialog.DialogUtil
import com.example.movieapp.view.listener.ItemTouchHelperCallback
import com.example.movieapp.view.listener.ItemTouchHelperListener
import com.example.movieapp.view.listener.OnClickMovieListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>(), ItemTouchHelperListener {
    override val layoutId: Int
        get() = R.layout.fragment_favorite

    val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var itemTouchHelper: ItemTouchHelper

    private val movieAdapter by lazy {
        FavoriteMovieAdapter(requireContext(), object : OnClickMovieListener {
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
        (requireActivity() as MainActivity).setSupportActionBar(binding.favoriteTb)
        with(binding) {
            favoriteRv.setHasFixedSize(true)
            favoriteRv.adapter = movieAdapter
            itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(this@FavoriteFragment))
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

    /**
     * RecyclerView Item??? Drag&Drop??? ???????????? ??????
     * @param fromPosition Drag??? ????????? Position
     * @param toPosition Drop??? Position
     */
    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val item = movieAdapter.getMovieList()[fromPosition]
        movieAdapter.getMovieList().removeAt(fromPosition)
        movieAdapter.getMovieList().add(toPosition, item)
        /* ?????? ?????? */
        movieAdapter.getMovieList().forEachIndexed { index, movie ->
            movie.rank = index
        }
        mainViewModel.changeFavoriteMovieRank(movieAdapter.getMovieList(), fromPosition, toPosition) {
            movieAdapter.notifyItemMoved(fromPosition, toPosition)
        }

        return true
    }

    /**
     * ???????????? ?????? ????????? ???????????? ?????????????????? ???????????? ??????
     * @param movie ???????????? ???????????? ????????? Movie
     * @param position ?????????????????? ????????? Movie??? Position
     */
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

    /**
     * ??????????????? ????????? ???????????? ??????
     */
    fun update() {
        movieAdapter.allClear()
        mainViewModel.getFavoriteMovieList()
    }

    /**
     * onBackPressedCallBack??? ???????????? ??????
     */
    fun setDispatcher() {
        onBackPressedCallBack?.let {
            requireActivity().onBackPressedDispatcher.addCallback(this, it)
        }
    }

    override fun onDetach() {
        onBackPressedCallBack?.remove()
        onBackPressedCallBack = null
        super.onDetach()
    }


    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragment()
        const val TAG = "FavoriteFragment"
    }
}