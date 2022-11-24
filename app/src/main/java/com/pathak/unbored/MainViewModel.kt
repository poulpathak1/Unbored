package com.pathak.unbored

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainViewModel: ViewModel() {

    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    fun updateUser() {
        firebaseAuthLiveData.updateUser()
    }

    fun signOut() {
        //Firebase.auth.signOut()
        FirebaseAuth.getInstance().signOut()
        //userLogout()
    }

}
