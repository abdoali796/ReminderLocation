package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class AuthenViewM:ViewModel() {

    enum class AuthenStata{
        AUTHEN,UNAUTHEN
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenStata.AUTHEN
        } else {
           AuthenStata.UNAUTHEN
        }
    }


}