package com.example.bloggingapp.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bloggingapp.modals.AccountProperties

@Dao
interface AccountPropertiesDao {

    //Making the methods
    @Insert(onConflict= OnConflictStrategy.REPLACE) //If a there is insert in user and if already exist then it current will be
    //updated with the new one
    fun insertAndReplace(accountProperties: AccountProperties):Long

    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insertOrIgnore(accountProperties: AccountProperties):Long //If the same value already exist in datbase then ignore it


    @Query("Select * FROM account_properties WHERE pk=:pk" )
    fun searchByPk(pk:Int):LiveData<AccountProperties>//This will select all from the account properties database which have a particular primary key

    @Query("Select * FROM account_properties WHERE email=:email" )
    fun searchByEmail(email:String):AccountProperties?

//Updating the account properties inside the database
        @Query("UPDATE account_properties SET email=:email,username=:username WHERE pk=:pk")
        fun updateAccountProperties(pk:Int,email: String,username:String)



}