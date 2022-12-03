package com.pathak.unbored

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class MainViewModel: ViewModel() {

    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private val dbHelp = ViewModelDBHelper()
    val favoritesList = MutableLiveData<MutableList<String>>()
    val acceptedList = MutableLiveData<MutableList<String>>()
    val totalCompletedByUser = MutableLiveData<Int>()
    val isAnonymous = MutableLiveData<Boolean>()

    private var displayName = MutableLiveData<String>()
    private var email = MutableLiveData<String>()
    private var uid = MutableLiveData<String>()


    fun observeDisplayName() : LiveData<String> {
        return displayName
    }
    fun observeEmail() : LiveData<String> {
        return email
    }
    fun observeUid() : LiveData<String> {
        return uid
    }

    fun updateUser() {
        firebaseAuthLiveData.updateUser()

        val currentUser = firebaseAuthLiveData.getCurrentUser()

        displayName.postValue(currentUser?.displayName ?: "")
        email.postValue(currentUser?.email ?: "")
        uid.postValue(currentUser?.uid ?: "")

        if (currentUser?.displayName.isNullOrBlank()){
            val profileUpdates =
                UserProfileChangeRequest.Builder().setDisplayName("Anonymous User").build()
            currentUser?.updateProfile(profileUpdates)
        }
        fetchIsAnonymous()
        fetchAccepted()
        fetchFavorites()
        fetchTotalCompletedByUser()
    }
    fun updateUser(newUsername: String?, newEmail: String?) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()
        val profileUpdates =
            UserProfileChangeRequest.Builder().setDisplayName(newUsername).build()
        if (newEmail != null) {
            currentUser?.updateEmail(newEmail)
        }
        currentUser?.updateProfile(profileUpdates)
        updateUser()
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

    fun observeTotalCompletedByUser(): LiveData<Int> {
        return totalCompletedByUser
    }

    fun addTotalCompletedByUser(){
        dbHelp.addTotalCompletedByUser()
        fetchTotalCompletedByUser()
    }

    fun fetchTotalCompletedByUser() {
        dbHelp.fetchTotalCompletedByUser(totalCompletedByUser)
    }

    fun signOut() {
        userLogOut()
        //Firebase.auth.signOut()
        FirebaseAuth.getInstance().signOut()
        firebaseAuthLiveData.updateUser()
        FirestoreAuthLiveData().updateUser()
        updateUser()
    }

    private fun userLogOut() {
        favoritesList.postValue(emptyList<String>().toMutableList())
        acceptedList.postValue(emptyList<String>().toMutableList())
        displayName.postValue("")
        email.postValue("")
        uid.postValue("")
    }


    fun createPermanentAuth(email: String, password: String, listener: OnCompleteListener<AuthResult>) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()
        val credential = EmailAuthProvider.getCredential(email, password)
        currentUser?.linkWithCredential(credential)?.addOnCompleteListener(listener)
    }

    fun fetchIsAnonymous() {
        val currentUser = firebaseAuthLiveData.getCurrentUser()
        isAnonymous.postValue(currentUser?.isAnonymous ?: true)
    }

    fun observeIsAnonymous(): LiveData<Boolean> {
        return isAnonymous
    }


}
