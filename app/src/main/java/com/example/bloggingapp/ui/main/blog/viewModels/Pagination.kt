package com.example.bloggingapp.ui.main.blog.viewModels

import android.util.Log
import com.example.bloggingapp.persistence.BlogPostDAO
import com.example.bloggingapp.ui.main.blog.state.BlogStateEvent
import com.example.bloggingapp.ui.main.blog.state.BlogViewState


fun BlogViewModel.resetPage(){
    val update = getCurrentStateOrNew()
    update.blogFields.page = 1
    setViewState(update)
}

fun BlogViewModel.loadFirstPage() {
    setQueryInProgress(true)
    setQueryExhausted(false)
    resetPage()
    setStateEvent(BlogStateEvent.BlogSearchEvent())
    Log.e("AppDebug", "BlogViewModel: loadFirstPage: ${viewState.value!!.blogFields.searchQuery}")
}

private fun BlogViewModel.incrementPageNumber(){
    val update = getCurrentStateOrNew()
    val page = update.copy().blogFields.page // get current page
    update.blogFields.page = page + 1
    setViewState(update)
}

fun BlogViewModel.nextPage(){
    Log.d("AppDebug", "nextPage: Entered")

    if(!viewState.value!!.blogFields.isQueryInProgress
        && !viewState.value!!.blogFields.isQueryExhausted){
        Log.d("AppDebug", "BlogViewModel: Attempting to load next page...")
        incrementPageNumber()
        setQueryInProgress(true)
        setStateEvent(BlogStateEvent.BlogSearchEvent())
    }
}

fun BlogViewModel.handleIncomingBlogListData(viewState: BlogViewState){
    Log.d("AppDebug", "BlogViewModel, DataState: ${viewState}")
    Log.d("AppDebug", "BlogViewModel, DataState: isQueryInProgress?: " +
            "${viewState.blogFields.isQueryInProgress}")
    Log.d("AppDebug", "BlogViewModel, DataState: isQueryExhausted?: " +
            "${viewState.blogFields.isQueryExhausted}")
    setQueryInProgress(viewState.blogFields.isQueryInProgress)
    setQueryExhausted(viewState.blogFields.isQueryExhausted)
    setBlogListData(viewState.blogFields.blogList)
}





