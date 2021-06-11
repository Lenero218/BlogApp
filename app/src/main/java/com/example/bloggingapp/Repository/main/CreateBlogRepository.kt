package com.example.bloggingapp.Repository.main

import androidx.lifecycle.LiveData
import com.example.bloggingapp.Repository.JobManager
import com.example.bloggingapp.Repository.NetworkBoundResource
import com.example.bloggingapp.Session.SessionManager
import com.example.bloggingapp.api.main.OpenApiMainService
import com.example.bloggingapp.api.main.responses.BlogCreateUpdateResponse
import com.example.bloggingapp.modals.AuthToken
import com.example.bloggingapp.modals.BlogPost
import com.example.bloggingapp.persistence.BlogPostDAO
import com.example.bloggingapp.ui.DataState
import com.example.bloggingapp.ui.Response
import com.example.bloggingapp.ui.ResponseType
import com.example.bloggingapp.ui.main.create_blog.state.CreateBlogViewState
import com.example.bloggingapp.util.AbsentLiveData
import com.example.bloggingapp.util.DateUtils
import com.example.bloggingapp.util.GenericApiResponse
import com.example.bloggingapp.util.SuccessHandling.Companion.RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao:BlogPostDAO,
    val sessionManager: SessionManager
):JobManager("CreateBlogRepository")
{
    private val TAG:String="AppDebug"

    fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body:RequestBody,
        image: MultipartBody.Part?
    ):LiveData<DataState<CreateBlogViewState>>{
        return object : NetworkBoundResource<BlogCreateUpdateResponse, BlogPost,CreateBlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){
            override suspend fun createCacheRequestAndReturn() {
                //Not applicable
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<BlogCreateUpdateResponse>) {

                if(!response.body.response.equals(RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER)) {
                    val updateBlogPost = BlogPost(
                        response.body.pk,
                        response.body.title,
                        response.body.slug,
                        response.body.body,
                        response.body.image,
                        DateUtils.convertServerStringDateToLong(response.body.date_updated),
                        response.body.username
                    )
                    updateLocalDb(updateBlogPost)

                }
                withContext(Main){
                    onCompleteJob(   //This will tell that yes you have created a succesfull blog Post
                        DataState.data(
                            null,
                            Response(response.body.response, ResponseType.Dialog())
                        )
                    )
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
               return openApiMainService.createBlog(
                   "Token ${authToken.token}",
                   title, body, image
               )
            }

            override fun setJob(job: Job) {
               addJob("createNewBlogPost",job)
            }

            override fun loadFromCache(): LiveData<CreateBlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
               cacheObject?.let{
                    blogPostDao.insert(it)
               }
            }

        }.asLiveData()
    }


}