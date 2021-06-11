package com.example.bloggingapp.Repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.bloggingapp.Repository.JobManager
import com.example.bloggingapp.Repository.NetworkBoundResource
import com.example.bloggingapp.Session.SessionManager
import com.example.bloggingapp.api.auth.NetworkResponses.LoginResponse
import com.example.bloggingapp.api.auth.NetworkResponses.RegistrationResponse
import com.example.bloggingapp.api.auth.OpenApiAuthService
import com.example.bloggingapp.modals.AccountProperties
import com.example.bloggingapp.modals.AuthToken
import com.example.bloggingapp.persistence.AccountPropertiesDao
import com.example.bloggingapp.persistence.AuthTokenDao
import com.example.bloggingapp.ui.DataState
import com.example.bloggingapp.ui.Response
import com.example.bloggingapp.ui.ResponseType
import com.example.bloggingapp.ui.auth.State.AuthViewState
import com.example.bloggingapp.ui.auth.State.LoginFeilds
import com.example.bloggingapp.ui.auth.State.RegistrationFeilds
import com.example.bloggingapp.util.AbsentLiveData
import com.example.bloggingapp.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.example.bloggingapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.example.bloggingapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.example.bloggingapp.util.GenericApiResponse
import com.example.bloggingapp.util.PreferenceKeys
import com.example.bloggingapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

class   AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager:SessionManager,
    val openApiAuthService: OpenApiAuthService,
    val sharedPrefrence:SharedPreferences,
    val editor:SharedPreferences.Editor

):JobManager("AuthRepository") {
    private val TAG = "AppDebug"
    private var repositoryJb: Job? = null


    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        Log.d(TAG, "attemptLogin: Started")

        val loginFeildErrors = LoginFeilds(email, password).isValidForLogin()
        Log.d(TAG, "attemptLogin: Checking for none()")

        if (!loginFeildErrors.equals(LoginFeilds.LoginError.none())) {
            return returnErrorResponse(loginFeildErrors, ResponseType.Dialog())
        }

        Log.d(TAG, "attemptLogin: Getting network Bound")
        return object : NetworkBoundResource<LoginResponse, Any,AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
        true,
            true,
            false
        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<LoginResponse>) {
                 Log.d(TAG, "handleApiSuccessResponse: ${response}")
                //If the credentials doesnot match the this also means that the server request got completed just credentials didn't match so in this
                //case also the successResponse will be ok

                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return OnErrorReturn(response.body.errorMessage, true, false)
                }


                //Don't care about the result just insert if not present alredy inside accountProperties table
                accountPropertiesDao.insertAndReplace(
                    AccountProperties(
                        response.body.pk,
                    response.body.email,
                        ""
                    )
                )

                //Now inserting into the Auth token table using auth token dao
                val result=authTokenDao.insert(
                    AuthToken(response.body.pk,
                    response.body.token)

                )
                if(result<0){
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN,ResponseType.Dialog())//For returning a specific dialogue
                        )

                    )
                }

                saveAuthenticatedUserToPrefs(email)




                //Now officially login

                onCompleteJob( //Here we are login in the user and getting the information in both the cases whetther we register or login it is going to
                    //add the information to the database
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )


            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {

                addJob("attemptLogin",job)
            }


            //Not used in this case
            override suspend fun createCacheRequestAndReturn() {
                TODO("Not yet implemented")
            }

            //Not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }
            //Not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
                TODO("Not yet implemented")
            }

        }.asLiveData()

    }



    fun checkPreviosUserAuth():LiveData<DataState<AuthViewState>>{
        val previousAuthUserEmail:String?=
            sharedPrefrence.getString(PreferenceKeys.PREVIOUS_AUTH_USER,null)
        if(previousAuthUserEmail.isNullOrBlank()){
            Log.d(TAG, "checkPreviosUserAuth: No previusly authenticated user found")
            return returnNoTokenFound()
        }
return object:NetworkBoundResource<Void,Any,AuthViewState>(
    sessionManager.isConnectedToTheInternet(),
    false,false,shouldLoadFromTheCache = false

){


    //Not used in this case
    override fun loadFromCache(): LiveData<AuthViewState> {
        return AbsentLiveData.create()
    }
    //Not used in this case
    override suspend fun updateLocalDb(cacheObject: Any?) {
        TODO("Not yet implemented")
    }

    override suspend fun createCacheRequestAndReturn() {
                   accountPropertiesDao.searchByEmail(previousAuthUserEmail).let{accountProperties->
                       accountProperties?.let{
                           if(accountProperties.pk > -1){
                               authTokenDao.searchByPk(accountProperties.pk).let{authToken1-> //This will return the primary key accrding to the input key
                                   if(authToken1!=null){
                                       onCompleteJob(
                                           DataState.data(
                                               data=AuthViewState(
                                                   authToken=authToken1
                                               )
                                           )
                                       )
                                       return


                                   }

                               }
                           }

                       }

                       Log.d(TAG,"CheckPreviousAuthUser:AuthToken Not Found.....")
                        onCompleteJob(DataState.data(
                            data=null,
                            response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        ))


                   }
    }

    override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<Void>) {
    }

    override fun createCall(): LiveData<GenericApiResponse<Void>> {

        return AbsentLiveData.create()

    }

    override fun setJob(job: Job) {
        addJob("checkPreviosUserAuth",job)
    }


}.asLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
return object:LiveData<DataState<AuthViewState>>(){
    override fun onActive() {
        super.onActive()
        value=DataState.data(
            data = null,
            response= Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,ResponseType.None())
        )
    }
}
    }


    private fun saveAuthenticatedUserToPrefs(email: String) {
editor.putString(PreferenceKeys.PREVIOUS_AUTH_USER,email)
        editor.apply()
    }

    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationFeildErrors =
            RegistrationFeilds(username, email, password, confirmPassword).isValidForRegistration()
        Log.d(TAG, "attemptRegistration: Starting Registration")
        if (!registrationFeildErrors.equals(RegistrationFeilds.RegistrationError.none())) {
            Log.d(TAG, "attemptRegistration: Error Exist")


           return returnErrorResponse(registrationFeildErrors, ResponseType.Dialog())


        }

        return object : NetworkBoundResource<RegistrationResponse,Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {

            //Not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }
            //Not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
                TODO("Not yet implemented")
            }

            override suspend fun createCacheRequestAndReturn() {
                TODO("Not yet implemented")
            }


            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return OnErrorReturn(response.body.errorMessage, true, false)
                }


                //Don't care about the result just insert if not present alredy inside accountProperties table
                accountPropertiesDao.insertAndReplace(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //Now inserting into the Auth token table using auth token dao
                val result=authTokenDao.insert(
                    AuthToken(response.body.pk,
                        response.body.token)

                )
                if(result<0){
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN,ResponseType.Dialog())//For returning a specific dialogue
                        )

                    )
                }

//Save email to the account Prefrences(email)


                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )


            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
               addJob("attemptRegistration",job)

            }

        }.asLiveData()


    }


    private fun returnErrorResponse(
        errorMessage: String,
        responseType: ResponseType.Dialog
    ): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        errorMessage,
                        responseType
                    )
                )
            }

        }


    }


}