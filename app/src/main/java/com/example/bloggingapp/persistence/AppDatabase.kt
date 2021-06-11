package com.example.bloggingapp.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bloggingapp.modals.AccountProperties
import com.example.bloggingapp.modals.AuthToken
import com.example.bloggingapp.modals.BlogPost

@Database(entities=[AuthToken::class,AccountProperties::class,BlogPost::class],version=1) //This is adding different table into the database
abstract class AppDatabase:RoomDatabase() {

abstract fun getAuthTokenDao():AuthTokenDao
abstract fun getAccountPropertiesDao():AccountPropertiesDao
abstract fun getBlogPostDao():BlogPostDAO

companion object{
    const val DATABASE_NAME="app_db"
}
}