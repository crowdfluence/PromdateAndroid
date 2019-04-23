package com.example.logan.promdate

import com.example.logan.promdate.data.DefaultResponse
import com.example.logan.promdate.data.FeedResponse
import com.example.logan.promdate.data.UserResponse
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = "http://ec2-35-183-247-114.ca-central-1.compute.amazonaws.com"

//TODO: https://stackoverflow.com/questions/35693680/how-to-send-form-data-in-retrofit2-android/35698175

//Requests that can be sent to the api
interface ServerInterface {
    //login
    @POST("php/authenticate.php")
    @FormUrlEncoded
    fun login(@Field("email") email: String,
              @Field("password") password: String): Call<DefaultResponse>

    //register
    @POST("php/register.php")
    @FormUrlEncoded
    fun register(@Field("email") email: String,
                 @Field("password") password: String,
                 @Field("password-retype") checkPassword: String,
                 @Field("first-name") firstName: String,
                 @Field("last-name") lastName: String?,
                 @Field("school-id") schoolId: Int,
                 @Field("gender") gender: String?,
                 @Field("grade") grade: Int): Call<DefaultResponse>

    //feed
    @GET("php/search.php")
    fun getFeed(@Query("token") token: String,
                @Query("max-users") maxUsers: Int,
                @Query("offset") offset: Int? = null,
                @Query("school-id") schoolId: Int? = null,
                @Query("name") name: String? = null,
                @Query("gender") gender: String? = null,
                @Query("grade-min") minGrade: Int? = null,
                @Query("grade-max") maxGrade: Int? = null,
                @Query("dress-id") dressId: Int? = null): Call<FeedResponse>

    //regenerate token
    @POST("php/regenToken.php")
    @FormUrlEncoded
    fun regenToken(@Field("token") token: String): Call<DefaultResponse>

    //get user
    @GET("php/user.php")
    fun getUser(@Query("token") token: String,
                @Query("user-id") userId: Int): Call<UserResponse>
}

//initializes the standard api accessor that is reused throughout the code
class ApiAccessor {
    var apiService: ServerInterface
    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ServerInterface::class.java)
    }
}