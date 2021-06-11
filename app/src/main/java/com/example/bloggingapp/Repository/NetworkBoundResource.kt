package com.example.bloggingapp.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.bloggingapp.modals.AuthToken
import com.example.bloggingapp.ui.DataState
import com.example.bloggingapp.ui.Response
import com.example.bloggingapp.ui.ResponseType
import com.example.bloggingapp.ui.auth.State.AuthViewState
import com.example.bloggingapp.util.Constants.Companion.NETWORK_TIMEOUT
import com.example.bloggingapp.util.Constants.Companion.TESTING_CACHE_DELAY
import com.example.bloggingapp.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.example.bloggingapp.util.ErrorHandling
import com.example.bloggingapp.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.example.bloggingapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.example.bloggingapp.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.example.bloggingapp.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import com.example.bloggingapp.util.GenericApiResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject,CacheObject,ViewStateType>(
    isNetworkAvailable:Boolean,
    isNetworkRequest:Boolean,
    shouldCancelIfNoInternet:Boolean, //Cancelling the job is no network
    shouldLoadFromTheCache:Boolean //Should the cache data be loaded
) {




    private val TAG="AppDebug"
    protected val result=MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job:CompletableJob
    protected lateinit var couroutineScope: CoroutineScope



    init{
        setJob(initNewJob())
        setValue(DataState.loading(true,null))


        if(shouldLoadFromTheCache){
            //This will load the cached data from the database along with the progress bar

            val dbSource=loadFromCache()
            result.addSource(dbSource){
                result.removeSource(dbSource)
                setValue(DataState.loading(true,cachedData = it))
            }
        }

        if(isNetworkRequest){

            if(isNetworkAvailable){
               doNetworkRequest()
            }
            else{
                if(shouldCancelIfNoInternet){
                    OnErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET,true,shouldUseToast = false)

                }
                else{
                    doCacheRequest()
                }


            }

        }
        else{
doCacheRequest()
        }




    }

    private fun doCacheRequest() {
        couroutineScope.launch {
            delay(TESTING_CACHE_DELAY)
            createCacheRequestAndReturn()
        }    }

    suspend  fun handleNetwordCall(response: GenericApiResponse<ResponseObject>?) {
        when(response){
            is GenericApiResponse.ApiSuccessResponse->{
            handleApiSuccessResponse(response)

            }

            is GenericApiResponse.ApiErrorResponse->{
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage} " )
                OnErrorReturn(response.errorMessage,true,false)
            }

            is GenericApiResponse.ApiEmptyResponse->{
                Log.e(TAG, "NetworkBoundResource: RequestReturnedNothing(Http 204 " )
                OnErrorReturn("HTTP 204 Returned Nothing",true,false)
            }




        }




    }


    fun onCompleteJob(dataState: DataState<ViewStateType>){
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)


        }


        
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
result.value=dataState
    }


    fun OnErrorReturn(errorMessage:String?,shouldUseDialog:Boolean,shouldUseToast:Boolean){
    var msg=errorMessage
    var useDialog=shouldUseDialog
    var responseType:ResponseType=ResponseType.None()
    if(msg==null){
        msg=ERROR_UNKNOWN
    }else if(ErrorHandling.isNetworkError(msg)){
msg= ERROR_CHECK_NETWORK_CONNECTION
        useDialog=false//Because we don't want the error dialog to pop up again and again

    }
        if(shouldUseToast){
            responseType=ResponseType.Toast()
        }


    if(useDialog){
        responseType=ResponseType.Dialog()
    }

        onCompleteJob(DataState.error(
            response = Response(
                message=msg,
                responseType=responseType
            )
        ))


}




    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        Log.d(TAG, "initJob called: ")
        job=Job()
        job.invokeOnCompletion(onCancelling = true,true,object : CompletionHandler{
            override fun invoke(cause: Throwable?) {
           if(job.isCancelled){
               Log.e(TAG,"Network Bound Resource Job has been cancelled")
               cause?.let{
                  OnErrorReturn(it.message,false,true,)
               }?:OnErrorReturn(ERROR_UNKNOWN,false,true)
           }else if(job.isCompleted){
                  Log.e(TAG,"Netword Bound Resource: Job has been completed....")
           }
            }

        })

        couroutineScope= CoroutineScope(IO+job)
        return job
    }

    private fun doNetworkRequest(){

        couroutineScope.launch {
            //simulate network delay for testing

            delay(TESTING_NETWORK_DELAY)

            withContext(Main){
                val apiResponse=createCall()
                result.addSource(apiResponse){response->
                    result.removeSource(apiResponse)
                    couroutineScope.launch{
                        handleNetwordCall(response)
                    }



                }
            }
        }


        GlobalScope.launch(IO){
            delay(NETWORK_TIMEOUT)
            if(!job.isCompleted){
                Log.e(TAG,"NetworkBoundResource: Job Network TimeOut")
                job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
            }




        }
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>
    abstract suspend fun createCacheRequestAndReturn()
    abstract suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<ResponseObject>)
    abstract fun createCall():LiveData<GenericApiResponse<ResponseObject>>
    abstract fun setJob(job:Job)
    abstract fun loadFromCache():LiveData<ViewStateType>
    abstract suspend fun updateLocalDb(cacheObject:CacheObject?) //This will take the network data and update the database table according to the fetched data


}