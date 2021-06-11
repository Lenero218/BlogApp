package com.example.bloggingapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.example.bloggingapp.R
import com.example.bloggingapp.ViewModals.ViewModelProviderFactory
import com.example.bloggingapp.ui.BaseActivity
import com.example.bloggingapp.ui.ResponseType
import com.example.bloggingapp.ui.auth.State.AuthStateEvent
import com.example.bloggingapp.ui.main.MainActivity
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject



class AuthActivity : BaseActivity(),

  NavController.OnDestinationChangedListener
{




    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }
    
    
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    lateinit var viewModel: AuthViewModel
    override fun displayProgressBar(bool: Boolean) {
        if(bool){
            progress_bar.visibility= View.VISIBLE
        }
        else{
            progress_bar.visibility=View.INVISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        Log.d(TAG, "onCreate: Starting the auth activity viewModel")
        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        Log.d(TAG, "onCreate: Destination change listen")
        findNavController(R.id.auth_nav_host_fragment).addOnDestinationChangedListener(this)//If our fragment is changed if there is any active
        //job it is going to be cancelled


           subscribeObserver()


    }

//This has been done to prevent the cancellation of the reauthentication so this way, first the jobs will be cancelled and then onResume will be called
    //and reauthentication will get started
    override fun onResume() {
        super.onResume()
        checkPreviosAuthUser()

    }

    private fun subscribeObserver() {
        Log.d(TAG, "subscribeObserver: Subscribe observing")
        viewModel.dataState.observe(this,Observer{dataState->
              onDataStateChanged(dataState)
            Log.d(TAG, "subscribeObserver: 1")
            dataState.data?.let{
                data->
                data.data?.let{event->
                    event.getContentIfNotHandled()?.let{
                        it.authToken?.let{
                            Log.d(TAG,"AuthActivity DataState")
                            viewModel.setAuthToken(it)
                        }
                    }

                }
                Log.d(TAG, "subscribeObserver: 2")

            }

        })

        viewModel.viewState.observe(this, Observer {
            it.authToken?.let{
                sessionManager.login(it)
            }
        })


        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "AuthActivity:subscribeObserver: AuthToken:${authToken} ")
            if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                navMainActivity()
            }
        })


    }

    fun checkPreviosAuthUser(){
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
    }



    private fun navMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun expandAppBar() {
//ignre
     }



}