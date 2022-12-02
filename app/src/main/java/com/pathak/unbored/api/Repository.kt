package com.pathak.unbored.api

class Repository(private val api: BoredApi) {

    suspend fun getActivity() = api.getActivity()

    suspend fun getActivityByKey(key: String) = api.getActivityByKey(key)

    suspend fun getActivityFiltered(
        type: String,
        participants: Float,
        minprice: Float,
        maxprice: Float,
        minaccessibility: Float,
        maxaccessibility: Float
        ) = api.getActivityFiltered(
        type,
        participants.toString(),
        minprice.toString(),
        maxprice.toString(),
        minaccessibility.toString(),
        maxaccessibility.toString()
        )
}