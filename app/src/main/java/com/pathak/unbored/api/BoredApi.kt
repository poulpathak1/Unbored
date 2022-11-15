package com.pathak.unbored.api

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BoredApi {

    @GET("api/activity")
    suspend fun getActivity() : BoredActivity

    @GET("api/activity?")
    suspend fun getActivityFiltered(
        @Query("type") type: String,
        @Query("participants") participants: String,
        @Query("price") price: String,
        @Query("accessibility") accessibility: String
    ) : BoredActivity

    //data class BoredResponse(val result: BoredActivity)

    companion object {

        var url = HttpUrl.Builder()
            .scheme("https")
            .host("boredapi.com")
            .build()

        fun create(): BoredApi = create(url)

        private fun create(httpUrl: HttpUrl): BoredApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BoredApi::class.java)
        }
    }
}