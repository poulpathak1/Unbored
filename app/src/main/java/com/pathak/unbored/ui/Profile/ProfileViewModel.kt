package com.pathak.unbored.ui.Profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.pathak.unbored.FirestoreAuthLiveData

class ProfileViewModel : ViewModel() {

    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    fun updateUser() {
        firebaseAuthLiveData.updateUser()
        displayName.postValue(FirebaseAuth.getInstance().currentUser?.displayName)
        email.postValue(FirebaseAuth.getInstance().currentUser?.email)
        uid.postValue(FirebaseAuth.getInstance().currentUser?.uid)
    }

    private var displayName = MutableLiveData("Uninitialized")
    private var email = MutableLiveData("Uninitialized")
    private var uid = MutableLiveData("Uninitialized")

    fun observeDisplayName() : LiveData<String> {
        return displayName
    }
    fun observeEmail() : LiveData<String> {
        return email
    }
    fun observeUid() : LiveData<String> {
        return uid
    }
}