package com.pathak.unbored

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ViewModelDBHelper() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "users"
    private val leaderboardCollection = "leaderboard"
    private val favActivityList = "favActivityList"
    private val acceptedActivityList = "acceptedActivityList"
    private val totalCompletedByUser = "totalCompletedByUser"
    private val scoresDocument = "scores"
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private val cu = firebaseAuthLiveData.getCurrentUser()

    fun addToLeaderBoard(userName: String, score: Number) {
        db.collection(leaderboardCollection).document()
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


    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/order-limit-data
//    private fun dbFetchPhotoMeta(sortInfo: SortInfo,
//                                 notesList: MutableLiveData<List<PhotoMeta>>) {
//        // XXX Write me and use limitAndGet
//        val fieldName: String = when(sortInfo.sortColumn){
//            SortColumn.TITLE -> "pictureTitle"
//            SortColumn.SIZE -> "byteSize"
//        }
//        val direction : Query.Direction = if (sortInfo.ascending) {
//            Query.Direction.ASCENDING
//        } else {
//            Query.Direction.DESCENDING
//        }
//        limitAndGet(db.collection(rootCollection).orderBy(fieldName, direction), notesList)
//    }

    // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
//    fun createPhotoMeta(
//        sortInfo: SortInfo,
//        photoMeta: PhotoMeta,
//        notesList: MutableLiveData<List<PhotoMeta>>
//    ) {
//        // You can get a document id if you need it.
//        //photoMeta.firestoreID = db.collection(rootCollection).document().id
//        // XXX Write me: add photoMeta
//        photoMeta.firestoreID = db.collection(rootCollection).document().id
//        db.collection(rootCollection)
//            .add(photoMeta)
//            .addOnSuccessListener {
//                Log.d(
//                    javaClass.simpleName,
//                    "Note create \"${(photoMeta.uuid)}\" id: ${photoMeta.firestoreID}"
//                )
//                dbFetchPhotoMeta(sortInfo, notesList)
//            }
//            .addOnFailureListener { e ->
//                Log.d(javaClass.simpleName, "Note create FAILED \"${(photoMeta.uuid)}\"")
//                Log.w(javaClass.simpleName, "Error ", e)
//            }
//    }

