package com.example.logan.promdate.data

import android.arch.paging.PositionalDataSource
import android.util.Log
import com.example.logan.promdate.ApiAccessor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class SinglesDataSource : PositionalDataSource<User>() {

    private val users: MutableList<User> = ArrayList()
    val api = ApiAccessor().apiService

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<User>) {
        val token = File("token.txt").readText()
        api.getFeed(token, params.requestedLoadSize, null, params.requestedStartPosition)
            .enqueue(object : Callback<FeedResponse> {

                override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                    Log.e("SingleDataSource", "Failed to get data!")
                }

                override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                    val singles = response.body()?.result?.unmatchedUsers
                    callback.onResult(singles ?: listOf(), 0, users.size)
                }
            })
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<User>) {
        val token = File("token.txt").readText()
        api.getFeed(token, params.loadSize, null, null).enqueue(object : Callback<FeedResponse> {

            override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                Log.e("SingleDataSource", "Failed to get data!")
            }

            override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                val singles = response.body()?.result?.unmatchedUsers
                callback.onResult(singles ?: listOf())
            }
        })
    }
}
