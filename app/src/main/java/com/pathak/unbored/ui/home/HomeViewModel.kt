package com.pathak.unbored.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pathak.unbored.api.BoredActivity
import com.pathak.unbored.api.BoredApi
import com.pathak.unbored.api.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

class HomeViewModel : ViewModel() {

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text

    private val boredApi = BoredApi.create()
    private val repository = Repository(boredApi)
    private val boredActivity = MutableLiveData<BoredActivity>()

    private val type = MutableLiveData<String>()
    private val participants = MutableLiveData<Int>()
    private val price = MutableLiveData<Double>()
    private val accessibility = MutableLiveData<Double>()

    init {
        netRefresh()
    }

    fun netRefresh() {
        Log.d("XXX", "netRefresh api: ${boredApi}")
        Log.d("XXX", "netRefresh repo: ${repository}")
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO) {
            val x = repository.getActivity()
            Log.d("XXX", "netRefresh: ${x.activity}")
            boredActivity.postValue(x)
        }
    }

    fun filterActivity() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO) {
            boredActivity.postValue(repository.getActivityFiltered(
                type.value.toString(),
                participants.value?.toInt() ?: 1,
                price.value?.toDouble() ?: 0.0,
                accessibility.value?.toDouble() ?: 0.0
            ))
        }
    }

    fun observeBoredActivity(): LiveData<BoredActivity> {
        return boredActivity
    }

    fun setType(type: String){
        this.type.value = type

    }
    fun observeType(): LiveData<String> {
        return type
    }

    fun setParticipants(participants: Int) {
        this.participants.value = participants
    }
    fun observeParticipants(): LiveData<Int> {
        return participants
    }

    fun setPrice(price: Double) {
        this.price.value = price
    }
    fun observePrice(): LiveData<Double> {
        return price
    }

    fun setAccessibility(accessibility: Double) {
        this.accessibility.value = accessibility
    }
    fun observeAccessibility(): LiveData<Double> {
        return accessibility
    }


}