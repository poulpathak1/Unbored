package com.pathak.unbored

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import okhttp3.internal.notifyAll

class ViewModelDBHelper() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "users"
    private val cu = FirestoreAuthLiveData().getCurrentUser()

    fun addFavorite(activityKey: String){
        //Log.d("PPP", "addFavorite: ${cu!!.uid}")
        if (cu == null){
            Log.d("PPP", "cu is Null")
        }
        else{
            db.collection(rootCollection).document(cu.uid).update(
                "favActivityKeys", FieldValue.arrayUnion(activityKey)
            )
                .addOnSuccessListener {
                    Log.d("PPP", "addFavorite: successful")
                }
                .addOnFailureListener {
                    val setFav = mapOf(Pair("favActivityKeys", arrayListOf(activityKey)))
                    db.collection(rootCollection).document(cu.uid).set(setFav)
                }
//        db.collection(rootCollection).document(cu!!.uid).update(
//            "favActivityKeys", FieldValue.arrayUnion(activityKey))
        }

    }

    fun removeFavorite(favoritesList: MutableLiveData<MutableList<String>>,
                       activityKey: String) {

        favoritesList.value?.remove(activityKey)
        favoritesList.postValue(favoritesList.value)
        db.collection(rootCollection).document(cu!!.uid).update("favActivityKeys", favoritesList.value)
    }

    fun addAccepted(activityKey: String) {
        if (cu == null) {
            Log.d("PPP", "cu is Null")
        } else {
            db.collection(rootCollection).document(cu.uid).update(
                "acceptedActivityKeys", FieldValue.arrayUnion(activityKey)
            )
                .addOnSuccessListener {
                    Log.d("PPP", "addFavorite: successful")
                }
                .addOnFailureListener {
                    val newKey = mapOf(Pair("acceptedActivityKeys", arrayListOf(activityKey)))
                    db.collection(rootCollection).document(cu.uid).set(newKey)
                }
        }
    }

    fun removeAccepted(
        acceptedList: MutableLiveData<MutableList<String>>,
        boredActivityKey: String
    ) {
        acceptedList.value?.remove(boredActivityKey)
        db.collection(rootCollection).document(cu!!.uid).update("acceptedActivityKeys", acceptedList.value)
    }



    fun fetchFavorites(favoritesList: MutableLiveData<MutableList<String>>) {
        var x: MutableList<String> = arrayListOf()
        cu?.let {
            db.collection(rootCollection).document(it.uid)
                .get()
                .addOnSuccessListener {
                    val result = it.data?.get("favActivityKeys") ?: null
                    if (result != null){
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
                    val result = it.data?.get("acceptedActivityKeys") ?: null
                    if (result != null) {
                        x = result as MutableList<String>
                    }
                    acceptedList.postValue(x)
                }
                .addOnFailureListener {

                }
        }
    }



//    fun fetchPhotoMeta(sortInfo: SortInfo,
//                       notesList: MutableLiveData<List<PhotoMeta>>) {
//        dbFetchPhotoMeta(sortInfo, notesList)
//    }
    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    // But be careful about how listener updates live data
    // and noteListener?.remove() in onCleared
//    private fun limitAndGet(query: Query,
//                            notesList: MutableLiveData<List<PhotoMeta>>) {
//        query
//            .limit(100)
//            .get()
//            .addOnSuccessListener { result ->
//                Log.d(javaClass.simpleName, "allNotes fetch ${result!!.documents.size}")
//                // NB: This is done on a background thread
//                notesList.postValue(result.documents.mapNotNull {
//                    it.toObject(PhotoMeta::class.java)
//                })
//            }
//            .addOnFailureListener {
//                Log.d(javaClass.simpleName, "allNotes fetch FAILED ", it)
//            }
//    }
    /////////////////////////////////////////////////////////////


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

    // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
//    fun removePhotoMeta(
//        sortInfo: SortInfo,
//        photoMeta: PhotoMeta,
//        photoMetaList: MutableLiveData<List<PhotoMeta>>
//    ) {
//        // XXX Write me.  Make sure you delete the correct entry
//        db.collection(rootCollection)
//            .document(photoMeta.firestoreID)
//            .delete()
//            .addOnSuccessListener {
//                Log.d(
//                    javaClass.simpleName,
//                    "Note delete \"${(photoMeta.uuid)}\" id: ${photoMeta.firestoreID}"
//                )
//                dbFetchPhotoMeta(sortInfo, photoMetaList)
//            }
//    }
}