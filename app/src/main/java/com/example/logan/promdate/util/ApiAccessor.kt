package com.example.logan.promdate.util

import com.example.logan.promdate.data.*
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = "http://ec2-35-183-247-114.ca-central-1.compute.amazonaws.com"

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
                 @Field("first-name") firstName: String,
                 @Field("last-name") lastName: String,
                 @Field("school-id") schoolId: Int): Call<DefaultResponse>

    //updateUser
    @Multipart
    @POST("php/updateUser.php")
    fun updateUser(@Part("token") token: RequestBody,
                   @Part("social-instagram") instagram: RequestBody,
                   @Part("social-snapchat") snapchat: RequestBody,
                   @Part("social-twitter") twitter: RequestBody,
                   @Part("bio") bio: RequestBody,
                   @Part("first-name") firstName: RequestBody,
                   @Part("last-name") lastName: RequestBody,
                   @Part("school-id") schoolId: RequestBody,
                   @Part("grade") grade: RequestBody,
                   @Part("gender") gender: RequestBody,
                   @Part image: MultipartBody.Part? = null): Call<UpdateResponse>

    //feed
    @GET("php/feed.php")
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
    @GET("php/getUser.php")
    fun getUser(@Query("token") token: String,
                @Query("id") userId: Int? = null): Call<UserResponse>

    //match with user
    @POST("php/match.php")
    @FormUrlEncoded
    fun matchUser(@Field("token") token: String,
                  @Field("partner-id") partnerId: Int,
                  @Field("action") action: Int): Call<DefaultResponse>

    //get notifications
    @GET("php/notifications.php")
    fun getNotifications(@Query("token") token: String): Call<NotificationResponse>
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