package com.pathak.unbored.ui.home

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pathak.unbored.MainViewModel
import com.pathak.unbored.api.BoredActivity
import com.pathak.unbored.api.BoredApi
import com.pathak.unbored.api.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val boredApi = BoredApi.create()
    private val repository = Repository(boredApi)
    private val boredActivity = MutableLiveData<BoredActivity>()

    private val type = MutableLiveData<String>()
    private val participants = MutableLiveData<Float>()
    private val price = MutableLiveData<Float>()
    private val maxPrice = MutableLiveData<Float>()
    private val minPrice = MutableLiveData<Float>()
    private val accessibility = MutableLiveData<Float>()
    private val maxAccessibility = MutableLiveData<Float>()
    private val minAccessibility = MutableLiveData<Float>()
    private val isFavorite = MutableLiveData<Boolean>()

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
                type.value ?: "social",
                participants.value ?: 1.0f,
                minPrice.value ?: 0.0f,
                maxPrice.value ?: 1.0f,
                minAccessibility.value ?: 0.0f,
                maxAccessibility.value ?: 1.0f
            ))
        }
    }

    fun updateAttributes(){
        setType(boredActivity.value?.type ?: "social")
        setParticipants(boredActivity.value?.participants ?: 1.0f)
        setPrice(boredActivity.value?.price ?: 0.0f)
        setAccessibility(boredActivity.value?.accessibility ?: 0.0f)
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

    fun setParticipants(participants: Float) {
        this.participants.value = participants
    }
    fun observeParticipants(): LiveData<Float> {
        return participants
    }

    fun setPrice(price: Float) {
        this.price.value = price
    }
    fun observePrice(): LiveData<Float> {
        return price
    }
    fun setMinPrice(price: Float) {
        this.minPrice.value = price
    }
    fun observeMinPrice(): LiveData<Float> {
        return minPrice
    }
    fun setMaxPrice(price: Float) {
        this.maxPrice.value = price
    }
    fun observeMaxPrice(): LiveData<Float> {
        return maxPrice
    }


    fun setAccessibility(accessibility: Float) {
        this.accessibility.value = accessibility
    }
    fun observeAccessibility(): LiveData<Float> {
        return accessibility
    }
    fun setMinAccessibility(accessibility: Float) {
        this.minAccessibility.value = accessibility
    }
    fun observeMinAccessibility(): LiveData<Float> {
        return minAccessibility
    }

    fun setMaxAccessibility(accessibility: Float) {
        this.maxAccessibility.value = accessibility
    }
    fun observeMaxAccessibility(): LiveData<Float> {
        return maxAccessibility
    }

    fun setIsFavorite(favoriteStatus: Boolean) {
        this.isFavorite.postValue(favoriteStatus)
    }
    fun observeIsFavorite(): MutableLiveData<Boolean> {
        return isFavorite
    }


}