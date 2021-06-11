package com.example.bloggingapp.ui

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.bloggingapp.R
import com.google.android.material.dialog.MaterialDialogs


    fun Activity.displayToast(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT)
    }

    fun Context.displaySuccessDialog(message:String){
        MaterialDialog(this)
            .show {
                title(R.string.text_success)
                message(text=message)
                positiveButton(R.string.text_ok)

            }
    }

    fun Activity.displayErrorToast(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT)
    }

    fun Activity.displayErrorDialog(message:String){
        MaterialDialog(this)
            .show {
                title(R.string.text_error)
                message(text=message)
                positiveButton(R.string.text_ok)

            }
    }

fun Activity.displayInfoDialog(message:String){
    MaterialDialog(this)
        .show {
            title(R.string.text_info)
            message(text=message)
            positiveButton(R.string.text_ok)
        }
}


fun Activity.areYouSureDialog(message:String,callback:AreYouSureCallback){

    MaterialDialog(this)
        .show{
            title(R.string.are_you_sure)
            message(text=message)
            negativeButton(R.string.text_cancel) {
                callback.cancel()
            }
            positiveButton(R.string.text_yes) {
                callback.proceed()
            }
        }

}
interface AreYouSureCallback{
    fun proceed(
    )
    fun cancel()
}


