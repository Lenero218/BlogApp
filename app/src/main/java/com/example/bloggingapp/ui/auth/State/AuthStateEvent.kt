package com.example.bloggingapp.ui.auth.State

sealed class AuthStateEvent{
    data class LoginAttemptEvent(val email:String,val password:String):AuthStateEvent()
    data class RegisterAttemptEvent(val email:String,val username:String,val password:String,val confirmPassword:String):AuthStateEvent()
    class CheckPreviousAuthEvent:AuthStateEvent()//For auto authentication
class None:AuthStateEvent()




}