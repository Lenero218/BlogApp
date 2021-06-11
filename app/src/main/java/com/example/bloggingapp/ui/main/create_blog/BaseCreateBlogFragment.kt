package com.example.bloggingapp.ui.main.create_blog

import android.content.Context
import android.os.Bundle


import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.example.bloggingapp.R
import com.example.bloggingapp.ViewModals.ViewModelProviderFactory
import com.example.bloggingapp.ui.DataStateChangedListener
import com.example.bloggingapp.ui.UICommunicationListener
import com.example.bloggingapp.ui.main.blog.viewModels.BlogViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseCreateBlogFragment : DaggerFragment(){

    val TAG: String = "AppDebug"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    @Inject
    lateinit var requestManager: RequestManager

lateinit var uiCommunicationListener: UICommunicationListener
lateinit var viewModel: CreateBlogViewModel
    lateinit var stateChangeListener: DataStateChangedListener



    //Initialisation of the function for the functions to get change
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigationBarWithNavController(R.id.createBlogFragment,activity as AppCompatActivity) //Have to work on the naming of the layouts that have been used inside

        viewModel=activity?.run{
            ViewModelProvider(this,providerFactory).get(CreateBlogViewModel::class.java)
        }?:throw Exception("Invalid Activity")
            cancelActiveJobs()
    }

    //Setting up the function so that main blog fragment will not get affected by change in fragment
    fun setupNavigationBarWithNavController(fragmentId:Int, activity: AppCompatActivity){
        val appBarConfiguration= AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }

     fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangedListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }


        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }


    }


}