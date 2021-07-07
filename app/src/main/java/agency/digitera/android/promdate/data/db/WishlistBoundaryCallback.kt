package agency.digitera.android.promdate.data.db

import agency.digitera.android.promdate.data.FeedResponse
import agency.digitera.android.promdate.data.entities.User
import agency.digitera.android.promdate.util.ApiAccessor
import agency.digitera.android.promdate.util.PagingRequestHelper
import android.util.Log
import androidx.paging.PagedList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class WishlistBoundaryCallback(private val db: WishlistDb, private val token: String) :
    PagedList.BoundaryCallback<User>() {

    private val api = ApiAccessor()
    private val executor = Executors.newSingleThreadExecutor()
    private val helper = PagingRequestHelper(executor)

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()

        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) { helperCallback ->
            api.apiService.getFeed(token, 3 * PAGE_SIZE) //TODO: Get token
                .enqueue(object : Callback<FeedResponse> {

                    override fun onFailure(call: Call<FeedResponse>?, t: Throwable) {
                        Log.e("SingleBoundaryCallback", "Failed to load data!")
                        helperCallback.recordFailure(t)
                    }

                    override fun onResponse(
                        call: Call<FeedResponse>?,
                        response: Response<FeedResponse>
                    ) {
                        val users = response.body()?.result?.wishlist
                        executor.execute {
                            db.userDao().insert(users ?: listOf())
                            helperCallback.recordSuccess()
                        }
                        maxLoaded = users?.size ?: 0
                    }
                })
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: User) {
        super.onItemAtEndLoaded(itemAtEnd)

        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) { helperCallback ->
            api.apiService.getFeed(
                token,
                PAGE_SIZE, wishlistOffset = maxLoaded
            ) //TODO: Fix
                .enqueue(object : Callback<FeedResponse> {

                    override fun onFailure(call: Call<FeedResponse>?, t: Throwable) {
                        Log.e("CoupleBoundaryCallback", "Failed to load data!")
                        helperCallback.recordFailure(t)
                    }

                    override fun onResponse(
                        call: Call<FeedResponse>?,
                        response: Response<FeedResponse>
                    ) {

                        val users = response.body()?.result?.wishlist
                        executor.execute {
                            db.userDao().insert(users ?: listOf())
                            helperCallback.recordSuccess()
                        }
                        maxLoaded += users?.size ?: 0
                        Log.d("SingleBoundaryCallback", "Database size: ${users?.size}, Max loaded: $maxLoaded")
                    }
                })
        }
    }

    companion object {
        const val PAGE_SIZE = 16
        var maxLoaded = 0
    }
}