package com.example.bloggingapp.di.main

import com.example.bloggingapp.Repository.main.AccountRepository
import com.example.bloggingapp.Repository.main.BlogRepository
import com.example.bloggingapp.Repository.main.CreateBlogRepository
import com.example.bloggingapp.Session.SessionManager
import com.example.bloggingapp.api.main.OpenApiMainService
import com.example.bloggingapp.modals.AccountProperties
import com.example.bloggingapp.persistence.AccountPropertiesDao
import com.example.bloggingapp.persistence.AppDatabase
import com.example.bloggingapp.persistence.BlogPostDAO
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {
@MainScope
@Provides
fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder):OpenApiMainService{
    return retrofitBuilder
        .build()
        .create(OpenApiMainService::class.java)
}


    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ):AccountRepository{
        return AccountRepository(
            openApiMainService,
            accountPropertiesDao,
            sessionManager
        )

    }


    @MainScope
    @Provides
    fun provideBlogPostDao(db:AppDatabase): BlogPostDAO{
        return db.getBlogPostDao()
    }

    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDAO: BlogPostDAO,
        sessionManager: SessionManager
    ):BlogRepository{
        return BlogRepository(openApiMainService,blogPostDAO,sessionManager)
    }

@MainScope
@Provides
fun provideCreateBlogRepository(
    openApiMainService: OpenApiMainService,
    blogPostDAO: BlogPostDAO,
    sessionManager: SessionManager
):CreateBlogRepository{
    return CreateBlogRepository(
        openApiMainService,blogPostDAO,
        sessionManager
    )
}




    }

