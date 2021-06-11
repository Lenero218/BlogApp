package com.example.bloggingapp.ui

interface DataStateChangedListener {
    fun onDataStateChanged(dataState: DataState<*>?)

    fun expandAppBar()


    fun hideSoftkeyboard()

    fun isStoragePermissionGranted():Boolean

}