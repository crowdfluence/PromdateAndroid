package com.example.logan.promdate.data

import android.arch.paging.PositionalDataSource
import android.util.Log
import com.example.logan.promdate.ApiAccessor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class SinglesDataSource(private val tokenDirectory: File?) : PositionalDataSource<User>() {

    private val users: MutableList<User> = ArrayList()
    val api = ApiAccessor().apiService

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<User>) {
        try {
            val token = File(tokenDirectory, "token.txt").readText()
            Log.d("SingleDataSource", "Request size: ${params.requestedLoadSize}")
            Log.d("SingleDataSource", "Request start position: ${params.requestedStartPosition}")
            api.getFeed(token, params.requestedLoadSize, params.requestedStartPosition)
                .enqueue(object : Callback<FeedResponse> {

                    override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                        Log.e(
                            "SingleDataSource",
                            "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                        )
                    }

                    override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                        val singles = response.body()?.result ?: FeedInnerResponse(listOf(), listOf(), 0, 0)
                        Log.d("SingleDataSource", "List size: ${singles.unmatchedUsers.size}, Position: ${params.requestedStartPosition}, TotalCount: ${singles.maxUnmatched}")
                        callback.onResult(singles.unmatchedUsers, 0, singles.maxUnmatched)
                    }
                })
        } catch (e: Exception) {
            throw Exception("No token found")
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<User>) {
        try {
            val token = File(tokenDirectory, "token.txt").readText()
            api.getFeed(token, params.loadSize).enqueue(object : Callback<FeedResponse> {

                override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                    Log.e("SingleDataSource",
                          "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                    )
                }

                override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                    val singles = response.body()?.result?.unmatchedUsers
                    callback.onResult(singles ?: listOf())
                }
            })
        } catch (e: Exception) {
            throw Exception("No token found")
        }
    }
}
