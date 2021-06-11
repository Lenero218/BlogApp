package com.example.bloggingapp.ui.auth.State

import com.example.bloggingapp.modals.AuthToken

//Building the ViewState
data class AuthViewState (
    var registration_feilds:RegistrationFeilds?= RegistrationFeilds(),
var loginFeilds: LoginFeilds?= LoginFeilds(),
    var authToken: AuthToken?=null
    )





//View state stores the information entered by the user inside and if change then he will not loose any data because the variables are stored inside the String
//It stored the information when user enter username and password and suddenly shift to the registration fragment then if he wish to go login fragment then the data will still be there data

data class RegistrationFeilds(
    val registration_name:String?=null,
    val registration_email:String?=null,
    val registration_password:String?=null,
    val registration_confirmPasswoerd:String?=null,
){

   class RegistrationError{
       companion object{
           fun mustFillAllFeilds():String{
               return "Must fill all the feilds."
           }

           fun passwordDoesNotMatch():String{
               return "Passwords Do not Match, Please reenter"
           }
           fun none():String{
               return " none"
           }
       }
   }

    fun isValidForRegistration():String{
        if(registration_name.isNullOrEmpty()||registration_email.isNullOrEmpty()||registration_name.isNullOrEmpty()||registration_confirmPasswoerd.isNullOrEmpty()){
            return RegistrationError.mustFillAllFeilds()
        }
        if(!registration_password.equals(registration_confirmPasswoerd)){
            return RegistrationError.passwordDoesNotMatch()
        }
        return RegistrationError.none()

    }




}

data class LoginFeilds(
    val login_email:String?=null,
    val login_password:String?=null
){
    class LoginError{
        companion object{
            fun mustFillAllObjects():String{
                return "Fill all the feilds"
            }
            fun none():String{
                return "none"
            }
        }
    }

    fun isValidForLogin():String{
        if(login_email.isNullOrEmpty()){
            return LoginFeilds.LoginError.mustFillAllObjects()
        }
        return LoginError.none()
    }

    override fun toString():String{
        return "LoginState(email=$login_email,password=$login_password"
    }
}