package com.example.bloggingapp.modals

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Entity(tableName="account_properties") //To make a table in SQlite database and name is account_properties
data class AccountProperties (


    //Creating the database after taking the info from the server
    @SerializedName("pk")
    @Expose
    @PrimaryKey(autoGenerate=false) //We are getting this primary key from the server
@ColumnInfo(name="pk")
    var pk:Int,


    @SerializedName("email")
    @Expose
     @ColumnInfo(name="email")
    var email:String,


    @SerializedName("username")
    @Expose
    @ColumnInfo(name="username")
    var username:String

    )