package com.pathak.unbored.api

class Repository(private val api: BoredApi) {

    suspend fun getActivity() = api.getActivity()

    suspend fun getActivityFiltered(
        type: String,
        participants: Int,
        price: Double,
        accessibility: Double
    ) = api.getActivityFiltered(
        type,
        participants.toString(),
        price.toString(),
        accessibility.toString())
}