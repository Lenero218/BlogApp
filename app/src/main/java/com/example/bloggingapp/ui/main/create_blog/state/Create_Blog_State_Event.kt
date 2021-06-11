package com.example.bloggingapp.ui.main.create_blog.state

import okhttp3.MultipartBody

sealed class Create_Blog_State_Event {

    data class CreateNewBlogEvent(
        val title:String,
        val body:String,
        val image:MultipartBody.Part
    ):Create_Blog_State_Event()

    class None: Create_Blog_State_Event()

}