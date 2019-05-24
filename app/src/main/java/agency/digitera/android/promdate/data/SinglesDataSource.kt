package agency.digitera.android.promdate.data

import androidx.paging.PositionalDataSource
import android.util.Log
import agency.digitera.android.promdate.util.ApiAccessor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SinglesDataSource(private val token: String) : PositionalDataSource<User>() {

    private val api = ApiAccessor().apiService

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<User>) {
        api.getFeed(token, params.requestedLoadSize, 0)
            .enqueue(object : Callback<FeedResponse> {

                override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                    Log.e(
                        "SinglesDataSource",
                        "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                    )
                }

                override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                    val singles = response.body()?.result ?: FeedInnerResponse(
                        listOf(),
                        listOf()
                    )
                    if (response.body()?.status != 200) {
                        Log.e(
                            "SinglesDataSource",
                            response.body()?.toString() ?: ("No response from the server")
                        )
                    }
                    callback.onResult(singles.singles, 0)
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
                val singles = response.body()?.result ?: FeedInnerResponse(
                    listOf(),
                    listOf()
                )
                if (response.body()?.status != 200) {
                    Log.e(
                        "SinglesDataSource",
                        response.body()?.toString()
                    )
                }
                callback.onResult(singles.singles)
            }
        })
    }
}
