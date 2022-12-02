package com.pathak.unbored

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pathak.unbored.api.BoredActivity
import okhttp3.internal.notify

class MainViewModel: ViewModel() {

    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private val dbHelp = ViewModelDBHelper()
    val favoritesList = MutableLiveData<MutableList<String>>()
    val acceptedList = MutableLiveData<MutableList<String>>()


    fun updateUser() {
        firebaseAuthLiveData.updateUser()
        fetchAccepted()
        fetchFavorites()
    }
    fun updateUser(newUsername: String?, newEmail: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates =
            UserProfileChangeRequest.Builder().setDisplayName(newUsername).build()
        if (newEmail != null) {
            user?.updateEmail(newEmail)
        }
        user?.updateProfile(profileUpdates)
    }


    fun addFavorite(activityKey: String){
        dbHelp.addFavorite(activityKey)
        fetchFavorites()
    }

    fun removeFavorite(activityKey: String) {
        dbHelp.removeFavorite(favoritesList, activityKey)
    }


    fun addAccepted(activityKey: String) {
        dbHelp.addAccepted(activityKey)
        fetchAccepted()
    }

    fun removeAccepted(boredActivityKey: String) {
        dbHelp.removeAccepted(acceptedList, boredActivityKey)
    }

    fun observeFavoritesList(): MutableLiveData<MutableList<String>> {
        return favoritesList
    }

    fun observeAcceptedList(): MutableLiveData<MutableList<String>> {
        return acceptedList
    }

    fun fetchFavorites() {
        dbHelp.fetchFavorites(favoritesList)
    }

    fun fetchAccepted(){
        dbHelp.fetchAccepted(acceptedList)
    }

    fun signOut() {
        Firebase.auth.signOut()
        FirebaseAuth.getInstance().signOut()
        userLogOut()

        //userLogout()
    }

    private fun userLogOut() {
        favoritesList.postValue(emptyList<String>().toMutableList())
        acceptedList.postValue(emptyList<String>().toMutableList())
    }

    fun isFavorite(boredActivity: BoredActivity): Boolean {
        return observeFavoritesList().value?.contains(boredActivity.key) ?: false
    }




}
