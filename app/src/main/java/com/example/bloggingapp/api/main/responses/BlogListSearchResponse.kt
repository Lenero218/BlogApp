package com.example.bloggingapp.api.main.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BlogListSearchResponse(
    @SerializedName("results")
    @Expose
    var result: List<BlogSearchResponse>,


    @SerializedName("detail")
    @Expose
    var detail: String,

) {

    override fun toString(): String {
        return "BlogListSearchResponse(results=$result,detail=$detail)"
    }
}