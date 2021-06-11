package com.example.bloggingapp.Repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.bloggingapp.Repository.JobManager
import com.example.bloggingapp.Repository.NetworkBoundResource
import com.example.bloggingapp.Session.SessionManager
import com.example.bloggingapp.api.GenericResponse
import com.example.bloggingapp.api.main.OpenApiMainService
import com.example.bloggingapp.di.DaggerAppComponent
import com.example.bloggingapp.modals.AccountProperties
import com.example.bloggingapp.modals.AuthToken
import com.example.bloggingapp.persistence.AccountPropertiesDao
import com.example.bloggingapp.ui.DataState
import com.example.bloggingapp.ui.Response
import com.example.bloggingapp.ui.ResponseType
import com.example.bloggingapp.ui.main.account.state.AccountViewState
import com.example.bloggingapp.util.AbsentLiveData
import com.example.bloggingapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository

@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
):JobManager("AccountRepository")
{
    private val TAG="AppDebug"


    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>>{
        return object: NetworkBoundResource<AccountProperties,AccountProperties,  AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            false,
            true
        ){
            override suspend fun createCacheRequestAndReturn() {
                //If the network is down then end the transaction and view the cache
                withContext(Main){
                    //Finish by viewing the db cache
                    result.addSource(loadFromCache()){
                        viewState->
                        onCompleteJob(DataState.data(
                           data= viewState,
                            null
                        ))
                    }
                }



            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<AccountProperties>) {
                //This function will be called when the response is successful

                // Get data from network, 2. update the data and 3. Complete the job

                updateLocalDb(response.body)
                withContext(Main){
                    //finish by viewing the cache

                   createCacheRequestAndReturn()

                }






            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService   //here we are accensing the information frm the api
                    .getAccountProperties(
                        "Token ${authToken.token}"
                    )
            }

            override fun setJob(job: Job) {
                addJob("getAccountProperties",job)



            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap{it->
                        object :LiveData<AccountViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value=AccountViewState(it)
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
               cacheObject?.let{
                 accountPropertiesDao.updateAccountProperties(
                     cacheObject.pk,
                     cacheObject.email,
                     cacheObject.username
                 )
                }            }

        }.asLiveData()
    }


fun saveAccountProperties(
    authToken:AuthToken,
    accountProperties: AccountProperties
):LiveData<DataState<AccountViewState>>{
return object :NetworkBoundResource<GenericResponse,Any,AccountViewState>(
    sessionManager.isConnectedToTheInternet(),
    true,
    true,
    false
){

  //Not applicable
    override suspend fun createCacheRequestAndReturn() {


    }

    override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<GenericResponse>) {

        //If this response is not null
        //This response here is not null because here we are entering only if we are not getting null response



        updateLocalDb(null)
//After updation cancelling transaction
        withContext(Main){
            //finish with succcess response
            onCompleteJob(
                DataState.data(
                    null,
                    Response(response.body.response,ResponseType.Toast())
                //This will the success message as toast which we have retrived from the api

                )
            )
        }


    }

    override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
    return openApiMainService.saveAccountProperties(
        "Token ${authToken.token!!}",
        accountProperties.email,
        accountProperties.username
    )


    }

    override fun setJob(job: Job) {
addJob("saveAccountProperties",job)
    }

    override fun loadFromCache(): LiveData<AccountViewState> {
    return AbsentLiveData.create()
    }

    override suspend fun updateLocalDb(cacheObject: Any?) {
return accountPropertiesDao.updateAccountProperties(
    accountProperties.pk,
    accountProperties.email,
    accountProperties.username
)
    }

}.asLiveData()




}


    fun updatePassword(
        authToken:AuthToken,
        oldPassword:String,
        newPassword:String,
        confirmPassword:String
    ):LiveData<DataState<AccountViewState>>

    {
        return object:NetworkBoundResource<GenericResponse,Any,AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
        true,
            true,
            false
        ){
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<GenericResponse>) {

                //IF there is a request in doing network connection
                withContext(Main){
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(response.body.response,ResponseType.Toast())
                        )
                    )
                }


            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
return openApiMainService.updatePassword(
    "Token ${authToken.token}",
    oldPassword,
    newPassword,
    confirmPassword


)



            }

            override fun setJob(job: Job) {
addJob("updatePassword",job)
            }
//Not applicable
            override fun loadFromCache(): LiveData<AccountViewState> {
return AbsentLiveData.create()
            }
//Not applicable
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }


        }.asLiveData()


    }







}