package com.example.bloggingapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bloggingapp.R
import com.example.bloggingapp.Session.SessionManager
import com.example.bloggingapp.modals.AccountProperties
import com.example.bloggingapp.ui.main.account.state.AccountStateEvent
import kotlinx.android.synthetic.main.fragment_account.*
import javax.inject.Inject

class AccountFragment : BaseAccountFragment(){

//@Inject  because we are getting the sesssiion manager inside the viewmodel
//lateinit var sessionManager: SessionManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

change_password.setOnClickListener {

    if(findNavController().currentDestination?.id==R.id.changePasswordFragment){
        findNavController().navigate(R.id.action_changePasswordFragment_to_home)
    }

    findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
}
        logout_button.setOnClickListener{
            viewModel.logout()// we are not using session Manager here because session manager is injected inside the viewModel
        }
 subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer {dataState->
            stateChangeListener.onDataStateChanged(dataState)
            dataState?.let{
                it.data?.let{data ->
                data.data?.let{event->
                    event.getContentIfNotHandled()?.let{viewState->
                        viewState.accountProperties?.let{accountProperties ->
                            Log.d(TAG, "AccountFragment: DataState:${accountProperties}")
                            viewModel.setAccountPropertiesData(accountProperties)

                        }

                    }

                }

                }
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner,Observer{viewState->
            viewState?.let{
                it.accountProperties?.let{

                    Log.d(TAG, "AccountFragment ViewState:${it} ")
                    setAccountDataFeilds(it)
                }
            }
        })




    }


    override fun onResume() {
        super.onResume()
        //In this as soon as the user reach the fragment then the cache data is going to load and get sset


        viewModel.setStateEvent(
            AccountStateEvent.GetAccountPropertiesEvent()
        )
    }

    private fun setAccountDataFeilds(accountProperties: AccountProperties)
    {
    email?.setText(accountProperties.email)
        username?.setText(accountProperties.username)

    }





    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_view_menu,menu)

            }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

            when(item.itemId){
                R.id.edit->{
                    Log.d(TAG, "onOptionsItemSelected: Update account fragment should be called")

                    if(findNavController().currentDestination?.id==R.id.updateAccountFragment){
                        findNavController().navigate(R.id.action_updateAccountFragment_to_home)
                    }


                findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
                    return true
                }

        }
        return super.onOptionsItemSelected(item)
    }
}