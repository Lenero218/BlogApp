package com.example.bloggingapp.ui.main.account.state

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.bloggingapp.Repository.main.AccountRepository
import com.example.bloggingapp.Session.SessionManager
import com.example.bloggingapp.modals.AccountProperties
import com.example.bloggingapp.ui.BaseViewModel
import com.example.bloggingapp.ui.DataState
import com.example.bloggingapp.ui.Loading
import com.example.bloggingapp.ui.auth.State.AuthStateEvent
import com.example.bloggingapp.ui.main.blog.state.BlogViewState
import com.example.bloggingapp.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager:SessionManager,
    val accountRepository: AccountRepository
):BaseViewModel<AccountStateEvent,AccountViewState>()
{

    private val TAG:String="AppDebug"


    override fun initNewViewState(): AccountViewState {
        //This is for initialising the new View State

        return AccountViewState()
    }

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>>
    {
//handling different state events
        when(stateEvent){
            is AccountStateEvent.GetAccountPropertiesEvent ->{

                //This will add teh cached token once logged in inside the application
             return sessionManager.cachedToken.value?.let{authToken->
    accountRepository.getAccountProperties(authToken)
}?:AbsentLiveData.create()


            }
            is AccountStateEvent.UpdateAccountPropertiesEvent-> {
                return sessionManager.cachedToken.value?.let{authToken->
authToken.account_pk?.let{pk->
    accountRepository.saveAccountProperties(
        authToken,
        AccountProperties(
            pk,
            stateEvent.email,
            stateEvent.username
        )
    )
}                }?:AbsentLiveData.create()
            }


            is AccountStateEvent.ChangePasswordEvent->{
                return sessionManager.cachedToken.value?.let{authToken->
                    accountRepository.updatePassword(
                        authToken,
                        stateEvent.currentPassword,
                        stateEvent.newPassword,
                        stateEvent.confirmPassword

                    )
                }?:AbsentLiveData.create()



            }


            is AccountStateEvent.None->{
                return object:LiveData<DataState<AccountViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value= DataState(
                            null,
                            Loading(false),
                            null

                        )
                    }
                }
            }

        }
    }



//These are for cancelling the current job event and cancelling the active jobs
    fun cancelActiveJobs(){
        Log.d(TAG, "cancelActiveJobs: VIEWMODEL cancellation")
        handlePendingData()
        accountRepository.cancelActiveJobs()
    }

    //For hiding the progress bar when the job is currently being active
    fun handlePendingData(){
        setStateEvent(AccountStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties){
        var update=getCurrentStateOrNew()
        if(update.accountProperties==accountProperties){
            return
        }

            update.accountProperties = accountProperties
            _viewState.value=update

    }

    fun logout(){
        sessionManager.logout()
    }



}