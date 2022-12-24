package com.example.movieapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.movieapp.R
import com.example.movieapp.view.dialog.ProgressDialog
import kotlinx.coroutines.flow.callbackFlow

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
    @get:LayoutRes
    abstract val layoutId: Int
    private lateinit var _binding: T
    val binding: T
        get() = _binding

    private var progressDialog: ProgressDialog? = null

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
        super.onDetach()
    }

    abstract fun initView()
    abstract fun initEvent()
    abstract fun subscribe()

    fun showProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = ProgressDialog(requireContext(), getString(R.string.str_loading))
        progressDialog?.show()
    }

    fun dismissProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }
}