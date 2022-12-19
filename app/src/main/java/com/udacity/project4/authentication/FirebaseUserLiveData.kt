package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserLiveData : LiveData<FirebaseUser?>() {
 private val firebaseUser= FirebaseAuth.getInstance()

    private val authState = FirebaseAuth.AuthStateListener { firebaseAuth ->
        value = firebaseAuth.currentUser}
    override fun onActive() {
        firebaseUser.addAuthStateListener(authState)
    }
    override fun onInactive() {
        firebaseUser.removeAuthStateListener(authState)
    }
}
