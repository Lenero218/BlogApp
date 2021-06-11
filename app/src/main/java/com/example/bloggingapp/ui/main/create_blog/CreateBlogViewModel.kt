package com.example.bloggingapp.ui.main.create_blog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.bloggingapp.Repository.main.CreateBlogRepository
import com.example.bloggingapp.Session.SessionManager
import com.example.bloggingapp.ui.BaseViewModel
import com.example.bloggingapp.ui.DataState
import com.example.bloggingapp.ui.Loading
import com.example.bloggingapp.ui.main.create_blog.state.CreateBlogViewState
import com.example.bloggingapp.ui.main.create_blog.state.Create_Blog_State_Event
import com.example.bloggingapp.ui.main.create_blog.state.Create_Blog_State_Event.*
import com.example.bloggingapp.util.AbsentLiveData
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    val createBlogRepository: CreateBlogRepository,
    val sessionManager: SessionManager
):BaseViewModel<Create_Blog_State_Event,CreateBlogViewState>()

{
    override fun initNewViewState(): CreateBlogViewState {
                   return CreateBlogViewState()
    }

    fun setNewBlogFeilds(title:String?,body:String?,uri: Uri?){
        val update=getCurrentStateOrNew()
        val newBlogFeilds=update.blogFeilds
        title?.let{
            newBlogFeilds.newBlogTitle=it
        }
        body?.let{
            newBlogFeilds.newBlogBody=it
        }
        uri?.let{
            newBlogFeilds.newImageUri=it
        }

        update.blogFeilds=newBlogFeilds
        setViewState(update)

    }

    fun clearNewBlogFeilds(){
        val update=getCurrentStateOrNew()
        update.blogFeilds= CreateBlogViewState.NewBlogFields()
        setViewState(update)
    }

    fun cancelActiveJobs(){
        createBlogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    fun getNewImageUri():Uri?{
        getCurrentStateOrNew().let{
                it.blogFeilds.let{
                    return it.newImageUri
                }
        }

    }








    override fun handleStateEvent(stateEvent: Create_Blog_State_Event): LiveData<DataState<CreateBlogViewState>> {
        when(stateEvent){
                        is CreateNewBlogEvent->{
                           return sessionManager.cachedToken.value?.let{
                               authToken ->
                               val title= RequestBody.create(
                                   MediaType.parse("text/plain"),
                                   stateEvent.title
                               )


                               val body= RequestBody.create(
                                   MediaType.parse("text/plain"),
                                   stateEvent.body
                               )

                               createBlogRepository.createNewBlogPost(
                                   authToken,
                                   title,
                                   body,
                                   stateEvent.image
                               )



                           }?:AbsentLiveData.create()
                        }
                         is None->{
                             return liveData {
                                 emit(
                                     DataState(
                                         null,
                                         Loading(isLoading = false),
                                         null
                                     )
                                 )
                             }
                         }
        }
    }


}