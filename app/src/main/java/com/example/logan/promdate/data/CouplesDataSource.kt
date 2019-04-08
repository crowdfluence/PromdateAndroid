package com.example.logan.promdate.data

import android.arch.paging.PositionalDataSource
import android.util.Log
import com.example.logan.promdate.ApiAccessor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CouplesDataSource(private val tokenDirectory: File?) : PositionalDataSource<Couple>() {

    private val couples: MutableList<Couple> = ArrayList()
    val api = ApiAccessor().apiService

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Couple>) {
        try {
            val token = File(tokenDirectory, "token.txt").readText()
            Log.d("SingleDataSource", "Request size: ${params.requestedLoadSize}")
            Log.d("SingleDataSource", "Request start position: ${params.requestedStartPosition}")
            api.getFeed(token, params.requestedLoadSize, params.requestedStartPosition)
                .enqueue(object : Callback<FeedResponse> {

                    override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                        Log.e(
                            "CouplesDataSource",
                            "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                        )
                    }

                    override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                        val users = response.body()?.result?.matchedUsers ?: listOf()
                        val couplesList: MutableList<Couple> = mutableListOf()
                        for (i in 0 until users.size step 2) {
                            val couple = Couple(users[i], users[i + 1])
                            couplesList.add(couple)
                        }
                        callback.onResult(couplesList, 0, response.body()?.result?.maxMatched ?: 0)
                    }
                })
        } catch (e: Exception) {
            throw Exception("No token found")
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Couple>) {
        try {
            val token = File(tokenDirectory, "token.txt").readText()
            api.getFeed(token, params.loadSize, params.startPosition).enqueue(object : Callback<FeedResponse> {

                override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                    Log.e(
                        "CouplesDataSource",
                        "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                    )
                }

                override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                    val users = response.body()?.result?.matchedUsers ?: listOf()
                    val couplesList: MutableList<Couple> = mutableListOf()
                    for (i in 0 until users.size step 2) {
                        val couple = Couple(users[i], users[i + 1])
                        couplesList.add(couple)
                    }
                    callback.onResult(couplesList)
                }
            })
        } catch (e: Exception) {
            throw Exception("No token found")
        }
    }
}