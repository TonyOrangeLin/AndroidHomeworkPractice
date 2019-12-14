package com.example.myapplication

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface SquareService {

    @Headers( "X-Parse-Application-Id:vqYuKPOkLQLYHhk4QTGsGKFwATT4mBIGREI2m8eD")
    @GET("login")
    fun loginCheck(@Query("username") username: String,
                      @Query("password") password: String):
            Observable<logindata>

    @Headers( "X-Parse-Application-Id:vqYuKPOkLQLYHhk4QTGsGKFwATT4mBIGREI2m8eD")//, "X-Parse-Session-Token: r:494d5d1cfd7a79c88ff6acf8d4bbf871")
    @PUT("users/{objectId}")
    fun updatedate(@Path("objectId")objectId: String, @Query("timezone") timez: String, @Header("X-Parse-Session-Token")token: String):
            Observable<updatedata>

    companion object {
        fun create(): SquareService {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create())
                .baseUrl("https://watch-master-staging.herokuapp.com/api/")
                .build()

            return retrofit.create(SquareService::class.java)
        }
    }
}