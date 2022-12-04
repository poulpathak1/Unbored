package com.pathak.unbored

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
    val leaderboardList = MutableLiveData<MutableList<String>>()
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
        dbHelp.addUserName(currentUser?.displayName.toString())
        fetchLeadershipList()
    }
    fun updateUserName(userName: String, listener: OnCompleteListener<Void>){
        val currentUser = firebaseAuthLiveData.getCurrentUser()
        val profileUpdates =
            UserProfileChangeRequest.Builder().setDisplayName(userName).build()
        currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener(listener)
        firebaseAuthLiveData.updateUser()
    }
    fun updateEmail(email: String, listener: OnCompleteListener<Void>) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()
        currentUser?.updateEmail(email)?.addOnCompleteListener(listener)
        firebaseAuthLiveData.updateUser()
    }

    fun createPermanentAuth(email: String, password: String, listener: OnCompleteListener<AuthResult>) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()
        val credential = EmailAuthProvider.getCredential(email, password)
        currentUser?.linkWithCredential(credential)?.addOnCompleteListener(listener)
        firebaseAuthLiveData.updateUser()
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

    fun observeLeaderboardList(): MutableLiveData<MutableList<String>> {
        return leaderboardList
    }

    fun fetchFavorites() {
        dbHelp.fetchFavorites(favoritesList)
    }

    fun fetchAccepted(){
        dbHelp.fetchAccepted(acceptedList)
    }

    fun fetchLeadershipList() {
        dbHelp.load(leaderboardList)
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

    fun fetchIsAnonymous() {
        val currentUser = firebaseAuthLiveData.getCurrentUser()
        isAnonymous.postValue(currentUser?.isAnonymous)
    }

    fun observeIsAnonymous(): LiveData<Boolean> {
        return isAnonymous
    }

    fun getGD(type: String): GradientDrawable {
        val colorMap = hashMapOf(
            "education" to Color.valueOf(0xE3342F).hashCode() ,
            "recreational" to Color.valueOf(0xf6993f).hashCode(),
            "social" to Color.valueOf(0xffed4a).hashCode(),
            "diy" to Color.valueOf(0x38c172).hashCode(),
            "charity" to Color.valueOf(0xf66d9b).hashCode(),
            "cooking" to Color.valueOf(0x3490dc).hashCode(),
            "relaxation" to Color.valueOf(0x6574cd).hashCode(),
            "music" to Color.valueOf(0x9561e2).hashCode(),
            "busywork" to Color.valueOf(0x4dc0b5).hashCode()
        )

        if (type.isNullOrBlank()) return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(Color.CYAN, Color.LTGRAY)
        )

        var gd = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            colorMap[type]?.let { intArrayOf(it, it, Color.LTGRAY) }
        )
        gd.cornerRadius = 2.0f
        return gd
    }



}
