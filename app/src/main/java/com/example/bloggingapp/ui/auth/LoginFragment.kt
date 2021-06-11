package com.example.bloggingapp.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.bloggingapp.R
import com.example.bloggingapp.modals.AuthToken
import com.example.bloggingapp.ui.auth.State.AuthStateEvent

import com.example.bloggingapp.ui.auth.State.LoginFeilds
import com.example.bloggingapp.util.GenericApiResponse
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : BaseAuthFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

subscribeObservers()

login_button.setOnClickListener{
    Log.d(TAG, "onViewCreated: Loggin button pressed")
   login()
    Log.d(TAG, "onViewCreated: Loggin function called")
}


    }
private fun login(){
    Log.d(TAG, "login: Setting the state event")
    viewModel.setStateEvent(
        AuthStateEvent.LoginAttemptEvent(
            input_email.text.toString(),
            input_password.text.toString()

        )
    )
}
        private fun subscribeObservers() {
            viewModel.viewState.observe(viewLifecycleOwner, Observer {
                it.loginFeilds?.let{loginFeilds ->
                    loginFeilds.login_email?.let{
                        input_email.setText(it)
                    }

                    loginFeilds.login_password?.let{
                        input_password.setText(it)
                    }

                }
            })
        }

        override fun onDestroyView() {
            super.onDestroyView()
            viewModel.setLoginFields(
                LoginFeilds(
                    input_email.text.toString(),
                    input_password .text.toString(),
                )
            )
        }

}