package com.example.bloggingapp.di.main

import androidx.lifecycle.ViewModel
import com.example.bloggingapp.ViewModals.ViewModelKey
import com.example.bloggingapp.ui.main.account.state.AccountViewModel
import com.example.bloggingapp.ui.main.blog.viewModels.BlogViewModel
import com.example.bloggingapp.ui.main.create_blog.CreateBlogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAuthViewModel(accountViewModel: AccountViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateBlogViewModel::class)
    abstract fun bindCreateBlogViewModel(createBlogViewModel: CreateBlogViewModel): ViewModel

}