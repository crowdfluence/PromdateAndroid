package com.example.logan.promdate.data

import androidx.paging.PositionalDataSource
import android.util.Log
import com.example.logan.promdate.ApiAccessor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class SinglesDataSource(private val token: String) : PositionalDataSource<User>() {

    private val api = ApiAccessor().apiService

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<User>) {
        Log.d("SingleDataSource", "Request size: ${params.requestedLoadSize}")
        Log.d("SingleDataSource", "Request start position: ${params.requestedStartPosition}")
        api.getFeed(token, params.requestedLoadSize, params.requestedStartPosition)
            .enqueue(object : Callback<FeedResponse> {

                override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                    Log.e(
                        "SinglesDataSource",
                        "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                    )
                }

                override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                    val singles = response.body()?.result ?: FeedInnerResponse(listOf(), listOf(), 0, 0)
                    callback.onResult(singles.unmatchedUsers, 0, singles.maxUnmatched)
                }
            })
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<User>) {
        api.getFeed(token, params.loadSize, params.startPosition).enqueue(object : Callback<FeedResponse> {
            override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                Log.e("SinglesDataSource",
                      "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                )
            }

            override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                val singles = response.body()?.result ?: FeedInnerResponse(listOf(), listOf(), 0, 0)
                callback.onResult(singles.unmatchedUsers)
            }
        })
    }
}
