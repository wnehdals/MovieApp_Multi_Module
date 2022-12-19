package com.example.movieapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
    @get:LayoutRes
    abstract val layoutId: Int
    lateinit var _binding: T
    val binding: T
        get() = _binding
    protected lateinit var backButtonCallBack: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initState()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding.unbind()
    }

    open fun initState() {
        initView()
        initEvent()
        subscribe()
    }

    override fun onDetach() {
        backButtonCallBack.remove()
        super.onDetach()
    }

    abstract fun initView()
    abstract fun initEvent()
    abstract fun subscribe()
}