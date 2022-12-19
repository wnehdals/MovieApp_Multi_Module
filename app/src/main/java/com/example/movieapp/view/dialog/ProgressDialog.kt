package com.example.movieapp.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.movieapp.R

class ProgressDialog(context: Context, message: String) {
    private var progressDialog: AlertDialog? = null
    private val progressMessage: TextView by lazy {
        view.findViewById(R.id.progress_message)
    }
    private var view: View = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null)

    init {
        var builder = AlertDialog.Builder(context, R.style.Theme_MovieApp_AlertDialog)
        builder.setCancelable(false)
        builder.setView(view)
        progressMessage.text = message
        progressDialog = builder.create()
    }
    fun show() {
        progressDialog?.show()
    }
    fun dismiss() {
        progressDialog?.dismiss()
    }
}