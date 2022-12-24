package com.example.movieapp.ui

import androidx.fragment.app.Fragment
import com.example.movieapp.R
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.databinding.ActivityMainBinding
import com.example.movieapp.ui.favorite.FavoriteFragment
import com.example.movieapp.ui.search.SearchFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(){
    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initView() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_search -> {
                    showFragment(SearchFragment.newInstance(), SearchFragment.TAG)
                    true
                }
                R.id.bottom_nav_favorite -> {
                    showFragment(FavoriteFragment.newInstance(), FavoriteFragment.TAG)
                    true
                }
                else -> false
            }
        }
        binding.bottomNav.selectedItemId = R.id.bottom_nav_search
    }

    override fun subscribe() {
    }

    override fun initEvent() {
    }
    private fun showFragment(fragment: Fragment, tag: String) {
        val findFragment = supportFragmentManager.findFragmentByTag(tag)
        supportFragmentManager.fragments.forEach { fm ->
            supportFragmentManager.beginTransaction().hide(fm).commitAllowingStateLoss()
        }
        findFragment?.let {
            supportFragmentManager.beginTransaction().show(it).commitAllowingStateLoss()
            when (it) {
                is SearchFragment ->  {
                    it.setDispatcher()
                    it.update()
                }
                is FavoriteFragment -> {
                    it.setDispatcher()
                    it.update()
                }
                else -> return
            }
        }?: kotlin.run {
            supportFragmentManager.beginTransaction()
                .add(R.id.nav_host_fragment, fragment, tag)
                .commitAllowingStateLoss()
        }
    }
}