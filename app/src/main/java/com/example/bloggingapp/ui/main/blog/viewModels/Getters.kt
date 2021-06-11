package com.example.bloggingapp.ui.main.blog.viewModels

import android.net.Uri
import com.example.bloggingapp.modals.BlogPost


fun BlogViewModel.getFilter(): String {
    getCurrentStateOrNew().let {
        return it.blogFields.filter
    }
}

fun BlogViewModel.getOrder(): String {
    getCurrentStateOrNew().let {
        return it.blogFields.order
    }
}

fun BlogViewModel.getSearchQuery(): String {
    getCurrentStateOrNew().let {
        return it.blogFields.searchQuery
    }
}

fun BlogViewModel.getPage(): Int{
    getCurrentStateOrNew().let {
        return it.blogFields.page
    }
}

fun BlogViewModel.getSlug(): String{
    getCurrentStateOrNew().let {
        it.viewBlogFields.blogPost?.let {
            return it.slug
        }
    }
    return ""
}

fun BlogViewModel.isAuthorOfBlogPost(): Boolean{
    getCurrentStateOrNew().let {
        return it.viewBlogFields.isAuthorOfBlogPost
    }
}


fun BlogViewModel.getBlogPost(): BlogPost {
    getCurrentStateOrNew().let {
        return it.viewBlogFields.blogPost?.let {
            return it
        }?: getDummyBlogPost()
    }
}

fun BlogViewModel.getDummyBlogPost(): BlogPost{
    return BlogPost(-1, "" , "", "", "", 1, "")
}

fun BlogViewModel.getUpdatedBlogUri(): Uri? {
    getCurrentStateOrNew().let {
        it.updatedBlogFields.updatedImageUri?.let {
            return it
        }
    }
    return null
}

