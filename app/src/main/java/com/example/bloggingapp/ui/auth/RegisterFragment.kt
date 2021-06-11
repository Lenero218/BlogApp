package com.example.bloggingapp.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.bloggingapp.R
import com.example.bloggingapp.ui.auth.State.AuthStateEvent
import com.example.bloggingapp.ui.auth.State.RegistrationFeilds
import com.example.bloggingapp.util.GenericApiResponse
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : BaseAuthFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register_button.setOnClickListener{
            register()
        }



       subscribeObservers()
    }
    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {

            it.registration_feilds?.let{registrationFeilds ->
                registrationFeilds.registration_email?.let{
                input_email.setText(it)
            }

                registrationFeilds.registration_name?.let{
                    input_username.setText(it)
                }


                registrationFeilds.registration_password?.let{
                    input_password.setText(it)
                }

                registrationFeilds.registration_confirmPasswoerd?.let{
                    input_password_confirm.setText(it)
                }

            }
        })
    }

    private fun register(){
        viewModel.setStateEvent(
            AuthStateEvent.RegisterAttemptEvent(
                input_email.text.toString(),
            input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.toString()
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFeilds(
                input_email. text.toString(),
            input_password.text.toString(),
                input_username.text.toString(),
                input_password_confirm.text.toString()
        )
        )
    }
}
