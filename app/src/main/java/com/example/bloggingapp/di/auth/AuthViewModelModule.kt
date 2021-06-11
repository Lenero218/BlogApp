package com.example.bloggingapp.di.auth

import androidx.lifecycle.ViewModel
import com.example.bloggingapp.ViewModals.ViewModelKey
import com.example.bloggingapp.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}