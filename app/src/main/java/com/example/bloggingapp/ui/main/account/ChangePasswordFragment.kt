package com.example.bloggingapp.ui.main.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bloggingapp.R
import com.example.bloggingapp.ui.DataState
import com.example.bloggingapp.ui.main.account.state.AccountStateEvent
import com.example.bloggingapp.util.SuccessHandling.Companion.RESPONSE_PASSWORD_UPDATE_SUCCESS
import kotlinx.android.synthetic.main.fragment_change_password.*


class ChangePasswordFragment : BaseAccountFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            update_password_button.setOnClickListener{
             viewModel.setStateEvent(
             AccountStateEvent.ChangePasswordEvent(
            input_current_password.text.toString(),
            input_new_password.text.toString(),
            input_confirm_new_password.text.toString()
        )
    )
}
subscribeObservers()

    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer {dataState->
            stateChangeListener.onDataStateChanged(dataState)
            if(dataState!=null){
                dataState.data?.let{
                    data ->
                    data.response?.let{
                        event ->

                        if(event.peekContent().message.equals(RESPONSE_PASSWORD_UPDATE_SUCCESS)){
                            stateChangeListener.hideSoftkeyboard()
                            findNavController().popBackStack()//For taking back to the acount fragmnt

                        }



                    }
                }
            }
        })
    }


}