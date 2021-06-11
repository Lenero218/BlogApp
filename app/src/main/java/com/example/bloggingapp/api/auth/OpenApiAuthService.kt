package com.example.bloggingapp.api.auth

import androidx.lifecycle.LiveData
import com.example.bloggingapp.api.auth.NetworkResponses.LoginResponse
import com.example.bloggingapp.api.auth.NetworkResponses.RegistrationResponse
import com.example.bloggingapp.util.GenericApiResponse
import com.example.bloggingapp.util.LiveDataCallAdapter
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OpenApiAuthService{
    //Before applying the methods we need to build repose, if the user is added correctly or not
    //During the time of login there is response and if failed then there is a response and then a response
    //for registering the user and 2 responses for the failure and success in registration
    //First building of them are necessary and then taking the values from the API

@POST("account/login")
@FormUrlEncoded
fun login(
    @Field("username")  email:String,
    @Field("password")  password:  String,


): LiveData<GenericApiResponse<LoginResponse>>
//In this we are getting the rsponse wheather the user is able to get the information as (Login response) considering this
//email and password, this will be returned as live date in form of Generic Api response who is reponsible
//for checking wheather the authencation has been completed or not


@POST("account/register")
@FormUrlEncoded
fun register(
    @Field("email") email: String,
    @Field("username") username: String,
    @Field("password") password: String,
    @Field("password2") password2: String,



):LiveData<GenericApiResponse<RegistrationResponse>>
//In this we are getting the live data response of type GenericApiResponse and seeing that if the user is able
//to get registered succesfully or not

}