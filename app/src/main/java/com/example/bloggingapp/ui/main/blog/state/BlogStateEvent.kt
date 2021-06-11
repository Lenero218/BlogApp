package com.example.bloggingapp.ui.main.blog.state

import okhttp3.MultipartBody

sealed class BlogStateEvent {
    class BlogSearchEvent:BlogStateEvent()
    class CheckAuthOfBlogPost:BlogStateEvent()
    class None:BlogStateEvent()
    class DeleteBlogPostEvent:BlogStateEvent()
    data class UpdateBlogPostEvent(
        var title:String,
        var body:String,
        val image:MultipartBody.Part?
    ):BlogStateEvent()

}