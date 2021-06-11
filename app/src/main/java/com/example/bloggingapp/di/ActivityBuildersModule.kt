package com.example.bloggingapp.di

import com.example.bloggingapp.di.auth.AuthModule
import com.example.bloggingapp.di.auth.AuthScope
import com.example.bloggingapp.di.auth.AuthViewModelModule
import com.example.bloggingapp.di.main.MainFragmentBuildersModule
import com.example.bloggingapp.di.main.MainModule
import com.example.bloggingapp.di.main.MainScope
import com.example.bloggingapp.di.main.MainViewModelModule
import com.example.bloggingapp.ui.auth.AuthActivity
import com.example.bloggingapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity




    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}