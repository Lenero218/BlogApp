package com.example.bloggingapp.Repository

import android.util.Log
import kotlinx.coroutines.Job

open class JobManager(
    private val className:String,
) {
    private val TAG:String="AppDebug"
    private val jobs:HashMap<String, Job > =HashMap()

    fun addJob(methodName:String,job: Job){
        CancelJob(methodName)
    jobs[methodName]=job
    }

    private fun CancelJob(methodName: String) {

        getJob(methodName)?.cancel()

    }

    fun getJob(methodName:String):Job?{
        if(jobs.containsKey(methodName)){
            jobs[methodName]?.let{
                return it
            }
        }
        return null
    }

    //Cancelling all the ongoing jobs whenever user switches to new graph
    fun cancelActiveJobs(){
        for((methodName,job) in jobs){
            if(job.isActive){
                Log.e(TAG, "$className:cancelling jobs in method: $methodName ", )
                job.cancel()
            }
        }
    }



}