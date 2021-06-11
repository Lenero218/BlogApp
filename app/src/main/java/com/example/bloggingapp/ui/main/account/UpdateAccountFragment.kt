package com.example.bloggingapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import com.example.bloggingapp.R
import com.example.bloggingapp.modals.AccountProperties
import com.example.bloggingapp.ui.DataState
import com.example.bloggingapp.ui.main.account.state.AccountStateEvent
import kotlinx.android.synthetic.main.fragment_update_account.*

class UpdateAccountFragment : BaseAccountFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)



        subscribeObserver()
    }

    private fun subscribeObserver(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer {dataState->
            stateChangeListener.onDataStateChanged(dataState)
            Log.d(TAG, "DataState: ${dataState}: ")
        })

viewModel.viewState.observe(viewLifecycleOwner,Observer{viewState->
    if(viewState!=null){
        viewState.accountProperties?.let{
            setAccountFeildDetails(it)
        }
    }
})


    }



    private fun setAccountFeildDetails(accountproperties:AccountProperties){
                input_email?.let{
                    input_email.setText(accountproperties.email.toString())
                }

        input_username?.let{
            input_username.setText(accountproperties.username.toString())
        }

    }


    private fun saveChanges(){
        viewModel.setStateEvent(
            AccountStateEvent.UpdateAccountPropertiesEvent(
                input_email.text.toString(),
                input_username.text.toString()
            )
        )

        stateChangeListener.hideSoftkeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.update_menu,menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when(item.itemId){
            R.id.save ->{
                saveChanges()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}