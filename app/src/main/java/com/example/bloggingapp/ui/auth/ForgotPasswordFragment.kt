package com.example.bloggingapp.ui.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.example.bloggingapp.R
import com.example.bloggingapp.ui.DataState
import com.example.bloggingapp.ui.DataStateChangedListener
import com.example.bloggingapp.ui.Response
import com.example.bloggingapp.ui.ResponseType
import com.example.bloggingapp.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ForgotPasswordFragment : BaseAuthFragment() {

lateinit var webView:WebView
lateinit var stateChangeListener:DataStateChangedListener
val webInteractionCallback: WebAppInterface.OnWeBInterationCallback= object:WebAppInterface.OnWeBInterationCallback{
    override fun onSuccess(email: String) {
        Log.d(TAG, "onSuccess: a reset link will be sent to {$email}")
        onPasswordResetLinkSent()

    }

    override fun onError(errorMessage: String) {
        Log.e(TAG, "onError: $errorMessage", )
 val dataState=DataState.error<Any>(
     response = Response(errorMessage,ResponseType.Dialog())
 )
        stateChangeListener.onDataStateChanged(
            dataState= dataState
        )

    }

    override fun onLoading(isLoading: Boolean) {
        Log.d(TAG, "onLoading: ....")

        GlobalScope.launch(Main) {
            stateChangeListener.onDataStateChanged(
                DataState.loading(isLoading,null)
            )
        }

    }

}

    private fun onPasswordResetLinkSent() {
        //Ensure that if you are updating the UI elements then always do it in the Main thread

        GlobalScope.launch(Main) {
            parent_view.removeView(webView)
            webView.destroy()

            val animation=TranslateAnimation(
                password_reset_done_container.width.toFloat(),
                0f,0f,0f
            )
            animation.duration=500
            password_reset_done_container.startAnimation(animation)
            password_reset_done_container.visibility=View.VISIBLE

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${viewModel}")
        webView=webview

        loadPasswordResetWebView()




        return_to_launcher_fragment.setOnClickListener{
            findNavController().popBackStack()
        }




    }


class WebAppInterface
    constructor(
        private val callback:OnWeBInterationCallback
    ){
        private val TAG:String="AppDebug"

    @JavascriptInterface
    fun onSuccess(email: String){
        callback.onSuccess(email)
    }

    @JavascriptInterface
    fun onError(errorMessage: String){
        callback.onError(errorMessage)
    }

    @JavascriptInterface
    fun onLoading(isLoading: Boolean){
        callback.onLoading(isLoading)
    }

    interface OnWeBInterationCallback{
        fun onSuccess(email:String)
        fun onError(errorMessage:String)
        fun onLoading(isLoading:Boolean)
    }
    }



    @SuppressLint("SetJavaScriptEnabled")
    fun loadPasswordResetWebView(){
        stateChangeListener.onDataStateChanged(
            DataState.loading(true,null)
        )
        webView.webViewClient=object:WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                stateChangeListener.onDataStateChanged(DataState.loading(false,null))//Remove the progress bar once loading

            }
        }
        webView.loadUrl(Constants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled=true
        //Build a java script interface that is going to interact with the javascript on server so that it can interact with the webView
        webView.addJavascriptInterface(WebAppInterface(webInteractionCallback),"AndroidTextListener")//This will create the connection
        //Web webview and the android because using this name we are getting information from the server side and calling the android defined
    }





    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {

            stateChangeListener=context as DataStateChangedListener


        }catch (e:ClassCastException){
            Log.e(TAG, "onAttach: $context must implement Data State Change Listener", )
        }

    }
}