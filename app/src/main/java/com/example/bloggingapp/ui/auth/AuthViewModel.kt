package com.example.bloggingapp.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.bloggingapp.Repository.auth.AuthRepository
import com.example.bloggingapp.api.auth.NetworkResponses.LoginResponse
import com.example.bloggingapp.api.auth.NetworkResponses.RegistrationResponse
import com.example.bloggingapp.modals.AuthToken
import com.example.bloggingapp.ui.BaseViewModel
import com.example.bloggingapp.ui.DataState
import com.example.bloggingapp.ui.auth.State.AuthStateEvent
import com.example.bloggingapp.ui.auth.State.AuthViewState
import com.example.bloggingapp.ui.auth.State.LoginFeilds
import com.example.bloggingapp.ui.auth.State.RegistrationFeilds
import com.example.bloggingapp.util.AbsentLiveData
import com.example.bloggingapp.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
): BaseViewModel<AuthStateEvent, AuthViewState>()
{
    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when(stateEvent){

            is AuthStateEvent.LoginAttemptEvent -> {
                return authRepository.attemptLogin(
                    stateEvent.email,
                    stateEvent.password
                )
            }

            is AuthStateEvent.RegisterAttemptEvent -> {
                return authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirmPassword
                )
            }

            is AuthStateEvent.CheckPreviousAuthEvent -> {
                return authRepository.checkPreviosUserAuth()
            }


            is AuthStateEvent.None ->{
                return object: LiveData<DataState<AuthViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(null, null)
                    }
                }
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setRegistrationFields(registrationFields: RegistrationFeilds){
        val update = getCurrentStateOrNew()
        if(update.registration_feilds == registrationFields){
            return
        }
        update.registration_feilds = registrationFields
        setViewState(update)
    }

    fun setLoginFields(loginFields: LoginFeilds){
        val update = getCurrentStateOrNew()
        if(update.loginFeilds == loginFields){
            return
        }
        update.loginFeilds = loginFields
        setViewState(update)
    }

    fun setAuthToken(authToken: AuthToken){
        val update = getCurrentStateOrNew()
        if(update.authToken == authToken){
            return
        }
        update.authToken = authToken
        setViewState(update)
    }

    fun cancelActiveJobs(){
        handlePendingData()
        authRepository.cancelActiveJobs()
    }

    fun handlePendingData(){
        setStateEvent(AuthStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}
