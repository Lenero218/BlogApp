package com.example.bloggingapp.di

import com.example.bloggingapp.ui.auth.ForgotPasswordFragment
import com.example.bloggingapp.ui.auth.LauncherFragment
import com.example.bloggingapp.ui.auth.LoginFragment
import com.example.bloggingapp.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

import kotlin.text.Typography.dagger


@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment
}


