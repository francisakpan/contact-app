package com.francis.week6.network

import android.content.Context
import android.net.ConnectivityManager


object Network {

    fun check(context: Context): Boolean{
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
        return info != null && info.isConnected
    }
}