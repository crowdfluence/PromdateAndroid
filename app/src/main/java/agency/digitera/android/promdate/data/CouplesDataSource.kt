package agency.digitera.android.promdate.data

import androidx.paging.PositionalDataSource
import android.util.Log
import agency.digitera.android.promdate.util.ApiAccessor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CouplesDataSource(private val token: String) : PositionalDataSource<List<User>>() {

    private val api = ApiAccessor().apiService

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<List<User>>) {
        api.getFeed(token, params.requestedLoadSize, couplesOffset = 0)
            .enqueue(object : Callback<FeedResponse> {

                override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                    Log.e(
                        "CouplesDataSource",
                        "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                    )
                }

                override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                    val couples = response.body()?.result?.couples ?: listOf()
                    if (response.body()?.status != 200) {
                        Log.e(
                            "CouplesDataSource",
                            response.body()?.toString()
                        )
                    }
                    callback.onResult(couples, 0)
                }
            })
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<List<User>>) {
        api.getFeed(token, params.loadSize, couplesOffset = params.startPosition).enqueue(object : Callback<FeedResponse> {

            override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                Log.e(
                    "CouplesDataSource",
                    "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                )
            }

            override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                val couples = response.body()?.result?.couples ?: listOf()
                if (response.body()?.status != 200) {
                    Log.e(
                        "CouplesDataSource",
                        response.body()?.toString()
                    )
                }
                callback.onResult(couples)
            }
        })
    }
}