package com.example.bloggingapp.ui

//Going to be observed
data class Loading(val isLoading:Boolean)


//Once data got succes or denied it is not going to be seen on the screen again and again thats
//we wrapped it inside the event class
data class Data<T>(val data: Event<T>?,val response:Event<Response>?)
//We are going to observe this that's why not handled in Event classs
data class StateError(val response:Response)

data class Response(val message:String?,val responseType:ResponseType)

sealed class ResponseType{
    class Toast: ResponseType()
    class Dialog: ResponseType()
    class None:ResponseType()
}





/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
// allows you to look at a liveData once to avoid redundant Toast/DataSetting etc. during configuration changes
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }

        //Usage of sealed classs
//        val responseType:ResponseType
//        when(responseType){
//            is ResponseType.Toast->{
//
//            }
//
//            //Like this way we are going to apply some functionality
//        }



    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

    override fun toString(): String {
        return "Event(content=$content, hasBeenHandled=$hasBeenHandled)"
    }

    companion object {

        private val TAG: String = "AppDebug"

        // we don't want an event if the data is null
        fun <T> dataEvent(data: T?): Event<T>? {
            data?.let {
                return Event(it)
            }
            return null
        }

        // we don't want an event if the response is null
        fun responseEvent(response: Response?): Event<Response>? {
            response?.let {
                return Event(response)
            }
            return null
        }
    }


}