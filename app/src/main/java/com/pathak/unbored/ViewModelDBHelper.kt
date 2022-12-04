package com.pathak.unbored

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewModelDBHelper() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "users"
    private val leaderboardCollection = "leaderboard"
    private val favActivityList = "favActivityList"
    private val acceptedActivityList = "acceptedActivityList"
    private val totalCompletedByUser = "totalCompletedByUser"
    private val userNameKey = "userName"
    private val scoresDocument = "scores"
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private val cu = firebaseAuthLiveData.getCurrentUser()

    fun addToLeaderBoard(userName: String, score: Number) {
        db.collection(leaderboardCollection).document()
    }

    fun addUserName(userName: String) {
        if (cu != null) {

            db.collection(rootCollection).document(cu.uid).update(
                userNameKey, cu.displayName
            )
                .addOnSuccessListener {

                }
                .addOnFailureListener {
                    val setUserName = mapOf(Pair(userNameKey, cu.displayName))
                    db.collection(rootCollection).document(cu.uid).set(setUserName)
                }
        }
    }

    fun load(leaderboardList: MutableLiveData<MutableList<String>>) {
        val scoreField: String = totalCompletedByUser
        val userNameField: String = userNameKey
        val transfer: MutableList<String> = mutableListOf()
        db.collection(rootCollection)
            .orderBy(scoreField, Query.Direction.DESCENDING)
            .whereGreaterThan(scoreField, 0)
            .get()
            .addOnSuccessListener {
                it.documents.mapNotNull {
                    //it.toObject(PhotoMeta::class.java)
                    val result = it

                    var userName: String
                    var userNameRaw = result.get(userNameField)
                    if (userNameRaw == null){
                        userName = "Anonymous User"
                    }
                    else {
                        userName = userNameRaw as String
                    }
                    var score = result.get(scoreField) as Long
                    var x = "$userName : $score"
                    transfer.add(x)
                    leaderboardList.postValue(transfer)
                }
            }
            .addOnFailureListener {
                Log.d("DDD", "load: failed", it)
            }

    }



    fun addFavorite(activityKey: String) {
        if (cu != null) {

            db.collection(rootCollection).document(cu.uid).update(
                favActivityList, FieldValue.arrayUnion(activityKey)
            )
                .addOnSuccessListener {
                    //Log.d("PPP", "addFavorite: successful")
                }
                .addOnFailureListener {
                    val setFav = mapOf(Pair(favActivityList, arrayListOf(activityKey)))
                    db.collection(rootCollection).document(cu.uid).set(setFav)
                }
        }

    }

    fun removeFavorite(
        favoritesList: MutableLiveData<MutableList<String>>,
        activityKey: String
    ) {

        favoritesList.value?.remove(activityKey)
        favoritesList.postValue(favoritesList.value)
        db.collection(rootCollection).document(cu!!.uid)
            .update(favActivityList, favoritesList.value)
    }


    fun fetchFavorites(favoritesList: MutableLiveData<MutableList<String>>) {
        var x: MutableList<String> = arrayListOf()
        cu?.let {
            db.collection(rootCollection).document(it.uid)
                .get()
                .addOnSuccessListener {
                    val result = it.data?.get(favActivityList)
                    if (result != null) {
                        x = result as MutableList<String>
                    }
                    favoritesList.postValue(x)
                }
                .addOnFailureListener {

                }
        }
    }

    fun addAccepted(activityKey: String) {
        if (cu == null) {
            //Log.d("PPP", "cu is Null")
        } else {
            db.collection(rootCollection).document(cu.uid).update(
                acceptedActivityList, FieldValue.arrayUnion(activityKey)
            )
                .addOnSuccessListener {
                    //Log.d("PPP", "addFavorite: successful")
                }
                .addOnFailureListener {
                    val newKey = mapOf(Pair(acceptedActivityList, arrayListOf(activityKey)))
                    db.collection(rootCollection).document(cu.uid).set(newKey)
                }
        }
    }

    fun removeAccepted(
        acceptedList: MutableLiveData<MutableList<String>>,
        boredActivityKey: String
    ) {
        acceptedList.value?.remove(boredActivityKey)
        db.collection(rootCollection).document(cu!!.uid)
            .update(acceptedActivityList, acceptedList.value)
    }



    fun fetchAccepted(acceptedList: MutableLiveData<MutableList<String>>) {
        var x: MutableList<String> = arrayListOf()
        cu?.let {
            db.collection(rootCollection).document(it.uid)
                .get()
                .addOnSuccessListener {
                    val result = it.data?.get(acceptedActivityList)
                    if (result != null) {
                        x = result as MutableList<String>
                    }
                    acceptedList.postValue(x)
                }
                .addOnFailureListener {

                }
        }
    }

    fun addTotalCompletedByUser() {
        if (cu != null) {
            db.collection(rootCollection).document(cu.uid).update(
                totalCompletedByUser, FieldValue.increment(1)
            )
                .addOnSuccessListener {

                }
                .addOnFailureListener {
                    val newKey = mapOf(Pair(totalCompletedByUser, 1))
                    db.collection(rootCollection).document(cu.uid).set(newKey)
                }
        }
    }

    fun fetchTotalCompletedByUser(totalCompletedByUser: MutableLiveData<Int>) {
        var x: Long = 0
        cu?.let {
            db.collection(rootCollection).document(it.uid)
                .get()
                .addOnSuccessListener {
                    val result = it.data?.get(this.totalCompletedByUser)
                    if (result != null) {
                        x = result as Long
                    }
                    totalCompletedByUser.postValue(x.toInt())
                }
                .addOnFailureListener {

                }
        }
    }
}

