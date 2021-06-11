package com.example.bloggingapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bloggingapp.Session.SessionManager
import com.example.bloggingapp.ui.main.blog.state.BlogStateEvent
import com.example.bloggingapp.util.Constants.Companion.PERMISSIONS_REQUEST_READ_STORAGE
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import javax.inject.Inject

abstract  class BaseActivity:DaggerAppCompatActivity(),DataStateChangedListener,UICommunicationListener {
 val TAG:String="AppDebug"

  @Inject
  lateinit var sessionManager:SessionManager


  override fun onUiMessageReceived(uiMessage: UIMessage) {
  when(uiMessage.uiMessageType){

   is UIMessageType.AreYouSureDialog->{
    areYouSureDialog(
     uiMessage.message,
     uiMessage.uiMessageType.callback
    )
   }

   is UIMessageType.Toast->{
    displayToast(uiMessage.message)
   }
   is UIMessageType.Dialog->{
    displayInfoDialog(uiMessage.message)
   }

   is UIMessageType.None ->{
    Log.i(TAG, "onUiMessageReceived: ${uiMessage.message}")
   }



  }
 }

 override fun onDataStateChanged(dataState: DataState<*>?) {
dataState?.let{
 GlobalScope.launch(Main) {
  displayProgressBar(it.loading.isLoading)

  it.error?.let{errorEvent->
   handleStateError(errorEvent)

  }

  it.data?.let{

    it.response?.let{responseEvent->
     handleStateResponse(responseEvent)

    }





  }

 }
}



 }

 private fun handleStateError(event: Event<StateError>) {
  event.getContentIfNotHandled()?.let{
 when(it.response.responseType){
  is ResponseType.Toast ->{
   it.response.message?.let{message->
    displayToast(message)
   }
  }

  is ResponseType.Dialog ->{
    it.response.message?.let{message->
     displayErrorDialog(message)

    }
  }

  is ResponseType.None ->{

Log.e(TAG,"handleStateError: ${it.response.message}")
}
  }
 }
}



 private fun handleStateResponse(event: Event<Response> ) {
  event.getContentIfNotHandled()?.let{
   when(it.responseType){
    is ResponseType.Toast ->{
     it.message?.let{message->
      displayToast(message)
     }
    }

    is ResponseType.Dialog ->{
     it.message?.let{message->
      displayErrorDialog(message)

     }
    }

    is ResponseType.None ->{

     Log.e(TAG,"handleStateError: ${it.message}")
    }
   }
  }
 }


 override fun hideSoftkeyboard() {
  if(currentFocus!=null){
   val inputMethodManager=getSystemService(
    Context.INPUT_METHOD_SERVICE
   ) as InputMethodManager
   inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken,0)

  }



 }

 override fun isStoragePermissionGranted(): Boolean {
  if (
   ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.READ_EXTERNAL_STORAGE
   ) != PackageManager.PERMISSION_GRANTED
   &&
   ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
   ) != PackageManager.PERMISSION_GRANTED
  ) {
   ActivityCompat.requestPermissions(
    this,
    arrayOf(
     Manifest.permission.READ_EXTERNAL_STORAGE,
     Manifest.permission.WRITE_EXTERNAL_STORAGE
    ),
    PERMISSIONS_REQUEST_READ_STORAGE
   )
   return false
  } else {
  return  true
  }


 }



 abstract  fun displayProgressBar(bool:Boolean)

 }


