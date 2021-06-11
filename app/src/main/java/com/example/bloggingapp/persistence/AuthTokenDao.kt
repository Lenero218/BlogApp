package com.example.bloggingapp.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bloggingapp.modals.AccountProperties
import com.example.bloggingapp.modals.AuthToken

@Dao
interface AuthTokenDao  {
@Insert(onConflict=OnConflictStrategy.REPLACE)
 fun insert(authToken: AuthToken):Long //This will return the row that got inserted corresponding to the token

 //This will be called when the persson will logout from app, will not delete the row, this will just update the token value
 @Query("UPDATE auth_token SET token=null WHERE account_pk=:pk")
 fun nullifyToken(pk:Int):Int //Will return the row which got updated

 @Query("Select * FROM AUTH_TOKEN WHERE account_pk=:pk" )
 fun searchByPk(pk:Int): AuthToken?//This will select all from the account properties database which have a particular primary key



}